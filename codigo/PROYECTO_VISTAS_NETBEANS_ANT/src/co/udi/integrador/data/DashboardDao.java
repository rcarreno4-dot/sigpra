package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class DashboardDao {
    private final RoleIdentityDao roleIdentityDao = new RoleIdentityDao();

    public record StudentDashboardData(String horasRegistradas, String horasValidadas, String estadoPractica,
                                       DefaultTableModel tasks) {
    }

    public record TeacherDashboardData(String estudiantesAsignados, String entradasPendientes, String practicasEnCurso,
                                       DefaultTableModel queue) {
    }

    public record DirectorDashboardData(String pendientesAprobacion, String enCurso, String finalizadas,
                                        DefaultTableModel statusByProgram) {
    }

    private static final String SQL_STUDENT_KPI = """
            SELECT NVL(SUM(b.horas_reportadas), 0) AS horas_registradas,
                   NVL(SUM(CASE WHEN b.estado_validacion = 'VALIDADA' THEN b.horas_reportadas ELSE 0 END), 0) AS horas_validadas,
                   NVL(MAX(p.estado), 'SIN_REGISTRO') AS estado_practica
            FROM practica p
            LEFT JOIN bitacora b ON b.id_practica = p.id_practica
            WHERE p.id_estudiante = ?
            """;

    private static final String SQL_STUDENT_TASKS = """
            SELECT TO_CHAR(b.fecha_actividad, 'YYYY-MM-DD') AS fecha_limite,
                   b.actividad AS tarea,
                   INITCAP(LOWER(b.estado_validacion)) AS estado
            FROM bitacora b
            JOIN practica p ON p.id_practica = b.id_practica
            WHERE p.id_estudiante = ?
            ORDER BY b.fecha_actividad DESC, b.id_bitacora DESC
            """;

    private static final String SQL_TEACHER_KPI = """
            SELECT COUNT(DISTINCT p.id_estudiante) AS estudiantes_asignados,
                   NVL(SUM(CASE WHEN b.estado_validacion = 'PENDIENTE' THEN 1 ELSE 0 END), 0) AS entradas_pendientes,
                   COUNT(DISTINCT CASE WHEN p.estado IN ('EN_CURSO', 'PENDIENTE_APROBACION') THEN p.id_practica END) AS practicas_en_curso
            FROM practica p
            LEFT JOIN bitacora b ON b.id_practica = p.id_practica
            WHERE p.id_docente = ?
            """;

    private static final String SQL_TEACHER_QUEUE = """
            SELECT TRIM(u.nombres || ' ' || u.apellidos) AS estudiante,
                   NVL(b.actividad, 'Sin actividades pendientes') AS actividad,
                   NVL(TO_CHAR(b.horas_reportadas), '-') AS horas,
                   CASE
                       WHEN b.id_bitacora IS NOT NULL THEN INITCAP(LOWER(b.estado_validacion))
                       WHEN p.estado = 'PENDIENTE_APROBACION' THEN 'Pend. aprobacion'
                       WHEN p.estado = 'EN_CURSO' THEN 'En curso'
                       ELSE INITCAP(LOWER(p.estado))
                   END AS estado
            FROM practica p
            JOIN estudiante e ON e.id_estudiante = p.id_estudiante
            JOIN usuario u ON u.id_usuario = e.id_usuario
            LEFT JOIN (
                SELECT id_practica, id_bitacora, actividad, horas_reportadas, estado_validacion
                FROM (
                    SELECT b.id_practica,
                           b.id_bitacora,
                           b.actividad,
                           b.horas_reportadas,
                           b.estado_validacion,
                           ROW_NUMBER() OVER (
                               PARTITION BY b.id_practica
                               ORDER BY b.fecha_actividad DESC, b.id_bitacora DESC
                           ) AS rn
                    FROM bitacora b
                    WHERE b.estado_validacion = 'PENDIENTE'
                )
                WHERE rn = 1
            ) b ON b.id_practica = p.id_practica
            WHERE p.id_docente = ?
              AND p.estado IN ('EN_CURSO', 'PENDIENTE_APROBACION')
            ORDER BY u.nombres, u.apellidos, p.id_practica DESC
            """;

    private static final String SQL_TEACHER_ASSIGNED_FALLBACK = """
            SELECT TRIM(u.nombres || ' ' || u.apellidos) AS estudiante,
                   p.estado
            FROM practica p
            JOIN estudiante e ON e.id_estudiante = p.id_estudiante
            JOIN usuario u ON u.id_usuario = e.id_usuario
            WHERE p.id_docente = ?
              AND p.estado IN ('EN_CURSO', 'PENDIENTE_APROBACION')
            ORDER BY u.nombres, u.apellidos, p.id_practica DESC
            """;

    private static final String SQL_DIRECTOR_KPI = """
            SELECT NVL(SUM(CASE WHEN p.estado = 'PENDIENTE_APROBACION' THEN 1 ELSE 0 END), 0) AS pendientes_aprobacion,
                   NVL(SUM(CASE WHEN p.estado = 'EN_CURSO' THEN 1 ELSE 0 END), 0) AS en_curso,
                   NVL(SUM(CASE WHEN p.estado = 'FINALIZADA' THEN 1 ELSE 0 END), 0) AS finalizadas
            FROM practica p
            """;

    private static final String SQL_DIRECTOR_PROGRAM = """
            SELECT e.programa,
                   NVL(SUM(CASE WHEN p.estado = 'PENDIENTE_APROBACION' THEN 1 ELSE 0 END), 0) AS pendientes_aprobacion,
                   NVL(SUM(CASE WHEN p.estado = 'EN_CURSO' THEN 1 ELSE 0 END), 0) AS en_curso,
                   NVL(SUM(CASE WHEN p.estado = 'FINALIZADA' THEN 1 ELSE 0 END), 0) AS finalizadas
            FROM practica p
            JOIN estudiante e ON e.id_estudiante = p.id_estudiante
            GROUP BY e.programa
            ORDER BY e.programa
            """;

    public StudentDashboardData loadStudentData(long studentUserId) throws SQLException {
        long studentId = roleIdentityDao.requireStudentIdByUserId(studentUserId);
        try (Connection cn = DatabaseConnection.getConnection()) {
            String horasRegistradas = "0";
            String horasValidadas = "0";
            String estado = "Sin registro";

            try (PreparedStatement ps = cn.prepareStatement(SQL_STUDENT_KPI)) {
                ps.setLong(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        horasRegistradas = rs.getString("horas_registradas");
                        horasValidadas = rs.getString("horas_validadas");
                        estado = normalizeStatus(rs.getString("estado_practica"));
                    }
                }
            }

            DefaultTableModel tasks = new DefaultTableModel(new String[]{"Tarea", "Fecha limite", "Estado"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            try (PreparedStatement ps = cn.prepareStatement(SQL_STUDENT_TASKS)) {
                ps.setLong(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tasks.addRow(new Object[]{
                                rs.getString("tarea"),
                                rs.getString("fecha_limite"),
                                rs.getString("estado")
                        });
                    }
                }
            }
            return new StudentDashboardData(horasRegistradas, horasValidadas, estado, tasks);
        }
    }

    public TeacherDashboardData loadTeacherData(long teacherUserId) throws SQLException {
        long teacherId = roleIdentityDao.requireTeacherIdByUserId(teacherUserId);
        try (Connection cn = DatabaseConnection.getConnection()) {
            String estudiantesAsignados = "0";
            String entradasPendientes = "0";
            String practicasEnCurso = "0";

            try (PreparedStatement ps = cn.prepareStatement(SQL_TEACHER_KPI)) {
                ps.setLong(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        estudiantesAsignados = rs.getString("estudiantes_asignados");
                        entradasPendientes = rs.getString("entradas_pendientes");
                        practicasEnCurso = rs.getString("practicas_en_curso");
                    }
                }
            }

            DefaultTableModel queue = new DefaultTableModel(new String[]{"Estudiante", "Actividad", "Horas", "Estado"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            try (PreparedStatement ps = cn.prepareStatement(SQL_TEACHER_QUEUE)) {
                ps.setLong(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        queue.addRow(new Object[]{
                                rs.getString("estudiante"),
                                rs.getString("actividad"),
                                rs.getString("horas"),
                                rs.getString("estado")
                        });
                    }
                }
            }

            if (queue.getRowCount() == 0 && !"0".equals(estudiantesAsignados)) {
                try (PreparedStatement ps = cn.prepareStatement(SQL_TEACHER_ASSIGNED_FALLBACK)) {
                    ps.setLong(1, teacherId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            queue.addRow(new Object[]{
                                    rs.getString("estudiante"),
                                    "Sin actividades pendientes",
                                    "-",
                                    normalizeStatus(rs.getString("estado"))
                            });
                        }
                    }
                }
            }
            return new TeacherDashboardData(estudiantesAsignados, entradasPendientes, practicasEnCurso, queue);
        }
    }

    public DirectorDashboardData loadDirectorData() throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            String pendientesAprobacion = "0";
            String enCurso = "0";
            String finalizadas = "0";

            try (PreparedStatement ps = cn.prepareStatement(SQL_DIRECTOR_KPI);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pendientesAprobacion = rs.getString("pendientes_aprobacion");
                    enCurso = rs.getString("en_curso");
                    finalizadas = rs.getString("finalizadas");
                }
            }

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Programa", "Pend. aprobacion", "En curso", "Finalizadas"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            try (PreparedStatement ps = cn.prepareStatement(SQL_DIRECTOR_PROGRAM);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("programa"),
                            rs.getString("pendientes_aprobacion"),
                            rs.getString("en_curso"),
                            rs.getString("finalizadas")
                    });
                }
            }
            return new DirectorDashboardData(pendientesAprobacion, enCurso, finalizadas, model);
        }
    }

    private String normalizeStatus(String dbStatus) {
        if (dbStatus == null || dbStatus.isBlank()) {
            return "Sin registro";
        }
        return switch (dbStatus) {
            case "EN_CURSO" -> "En curso";
            case "PENDIENTE" -> "Pendiente";
            case "PENDIENTE_APROBACION" -> "Pend. aprobacion directora";
            case "FINALIZADA" -> "Finalizada";
            default -> dbStatus;
        };
    }
}
