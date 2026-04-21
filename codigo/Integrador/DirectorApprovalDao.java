package Integrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class DirectorApprovalDao {
    private static final String SQL_PENDING = """
            SELECT p.id_practica,
                   TRIM(ue.nombres || ' ' || ue.apellidos) AS estudiante,
                   TRIM(ud.nombres || ' ' || ud.apellidos) AS docente,
                   p.periodo,
                   TO_CHAR(p.horas_acumuladas) || '/' || TO_CHAR(p.horas_objetivo) AS avance_horas,
                   SUM(CASE WHEN a.estado_validacion = 'VALIDADA' THEN 1 ELSE 0 END) AS actividades_validadas,
                   SUM(CASE WHEN a.estado_validacion = 'RECHAZADA' THEN 1 ELSE 0 END) AS actividades_rechazadas,
                   SUM(CASE WHEN a.estado_validacion = 'PENDIENTE' THEN 1 ELSE 0 END) AS actividades_pendientes,
                   NVL(
                       MAX(
                           CASE
                               WHEN a.comentario_docente IS NOT NULL AND TRIM(a.comentario_docente) <> '' THEN a.comentario_docente
                               ELSE NULL
                           END
                       ) KEEP (DENSE_RANK LAST ORDER BY a.fecha_actividad, a.id_actividad),
                       'Sin observacion'
                   ) AS observacion_docente,
                   TO_CHAR(p.fecha_inicio, 'YYYY-MM-DD') AS fecha_registro
            FROM practica p
            JOIN estudiante e ON e.id_estudiante = p.id_estudiante
            JOIN usuario ue ON ue.id_usuario = e.id_usuario
            JOIN docente_asesor da ON da.id_docente = p.id_docente
            JOIN usuario ud ON ud.id_usuario = da.id_usuario
            JOIN director_programa dp ON dp.programa = e.programa
            LEFT JOIN actividad_practica a ON a.id_practica = p.id_practica
            WHERE dp.id_usuario = ?
              AND p.estado = 'PENDIENTE_APROBACION'
            GROUP BY p.id_practica,
                     ue.nombres, ue.apellidos,
                     ud.nombres, ud.apellidos,
                     p.periodo,
                     p.horas_acumuladas, p.horas_objetivo,
                     p.fecha_inicio
            ORDER BY p.fecha_inicio DESC, p.id_practica DESC
            """;

    private static final String SQL_APPROVE = """
            UPDATE practica
            SET estado = 'FINALIZADA'
            WHERE id_practica = ?
            """;

    private static final String SQL_RETURN_TRACKING = """
            UPDATE practica
            SET estado = 'EN_CURSO'
            WHERE id_practica = ?
            """;

    public DefaultTableModel listPendingApprovals(long directorUserId) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "ID Practica",
                        "Estudiante",
                        "Docente",
                        "Periodo",
                        "Horas",
                        "Validadas",
                        "Rechazadas",
                        "Pendientes",
                        "Observacion docente",
                        "Fecha registro"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_PENDING)) {
            ps.setLong(1, directorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getLong("id_practica"),
                            rs.getString("estudiante"),
                            rs.getString("docente"),
                            rs.getString("periodo"),
                            rs.getString("avance_horas"),
                            rs.getInt("actividades_validadas"),
                            rs.getInt("actividades_rechazadas"),
                            rs.getInt("actividades_pendientes"),
                            rs.getString("observacion_docente"),
                            rs.getString("fecha_registro")
                    });
                }
            }
        }
        return model;
    }

    public void approveClosure(long practiceId, long directorId, String observation) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_APPROVE)) {
            ps.setLong(1, practiceId);
            ps.executeUpdate();
        }
    }

    public void returnToTracking(long practiceId, String observation) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_RETURN_TRACKING)) {
            ps.setLong(1, practiceId);
            ps.executeUpdate();
        }
    }
}
