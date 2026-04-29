package co.udi.integrador.data;

import co.udi.integrador.model.ComboItem;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PracticeDao {
    public record StudentProfile(long idEstudiante, long idUsuario, String nombre, String codigo, String programa) {
    }

    private static final String SQL_STUDENT_PROFILE_BY_USER = """
            SELECT e.id_estudiante,
                   e.id_usuario,
                   TRIM(u.nombres || ' ' || u.apellidos) AS nombre_completo,
                   e.codigo,
                   e.programa
            FROM estudiante e
            JOIN usuario u ON u.id_usuario = e.id_usuario
            WHERE e.id_usuario = ?
            """;

    private static final String SQL_STUDENT_PROFILE_BY_STUDENT = """
            SELECT e.id_estudiante,
                   e.id_usuario,
                   TRIM(u.nombres || ' ' || u.apellidos) AS nombre_completo,
                   e.codigo,
                   e.programa
            FROM estudiante e
            JOIN usuario u ON u.id_usuario = e.id_usuario
            WHERE e.id_estudiante = ?
            """;

    private static final String SQL_ALL_STUDENTS = """
            SELECT e.id_estudiante,
                   TRIM(u.nombres || ' ' || u.apellidos) || ' - ' || e.codigo AS label
            FROM estudiante e
            JOIN usuario u ON u.id_usuario = e.id_usuario
            WHERE u.rol = 'ESTUDIANTE'
            ORDER BY u.nombres, u.apellidos
            """;

    private static final String SQL_ALL_TEACHERS = """
            SELECT d.id_docente,
                   TRIM(u.nombres || ' ' || u.apellidos) AS label
            FROM docente_asesor d
            JOIN usuario u ON u.id_usuario = d.id_usuario
            WHERE u.estado = 'ACTIVO'
            ORDER BY u.nombres, u.apellidos
            """;

    private static final String SQL_ALL_DIRECTORS = """
            SELECT d.id_director,
                   TRIM(u.nombres || ' ' || u.apellidos) AS label
            FROM director_programa d
            JOIN usuario u ON u.id_usuario = d.id_usuario
            WHERE u.estado = 'ACTIVO'
            ORDER BY u.nombres, u.apellidos
            """;

    private static final String SQL_ALL_ENTITIES = """
            SELECT id_entidad, nombre AS label
            FROM entidad_receptora
            WHERE estado = 'ACTIVA'
            ORDER BY nombre
            """;

    private static final String SQL_INSERT_PRACTICE = """
            INSERT INTO practica (
                id_practica, id_estudiante, id_docente, id_entidad,
                periodo, fecha_inicio, fecha_fin, estado, horas_objetivo, horas_acumuladas
            ) VALUES (
                NULL, ?, ?, ?, ?, ?, ?, 'EN_CURSO', 160, 0
            )
            """;

    public StudentProfile findStudentProfileByUserId(long studentUserId) throws SQLException {
        return queryStudentProfile(SQL_STUDENT_PROFILE_BY_USER, studentUserId);
    }

    public StudentProfile findStudentProfileByStudentId(long studentId) throws SQLException {
        return queryStudentProfile(SQL_STUDENT_PROFILE_BY_STUDENT, studentId);
    }

    public StudentProfile findStudentProfile(long studentUserId) throws SQLException {
        return findStudentProfileByUserId(studentUserId);
    }

    private StudentProfile queryStudentProfile(String sql, long id) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new StudentProfile(
                        rs.getLong("id_estudiante"),
                        rs.getLong("id_usuario"),
                        rs.getString("nombre_completo"),
                        rs.getString("codigo"),
                        rs.getString("programa")
                );
            }
        }
    }

    public List<ComboItem> listStudents() throws SQLException {
        return listCombo(SQL_ALL_STUDENTS);
    }

    public List<ComboItem> listTeachers() throws SQLException {
        return listCombo(SQL_ALL_TEACHERS);
    }

    public List<ComboItem> listDirectors() throws SQLException {
        return listCombo(SQL_ALL_DIRECTORS);
    }

    public List<ComboItem> listEntities() throws SQLException {
        return listCombo(SQL_ALL_ENTITIES);
    }

    public void createPractice(long studentId, long teacherId, long directorId, long entityId,
                               String periodo, LocalDate fechaInicio, LocalDate fechaFin, String objetivo)
            throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT_PRACTICE)) {
            ps.setLong(1, studentId);
            ps.setLong(2, teacherId);
            ps.setLong(3, entityId);
            ps.setString(4, periodo);
            ps.setDate(5, Date.valueOf(fechaInicio));
            ps.setDate(6, Date.valueOf(fechaFin));
            ps.executeUpdate();
        }
    }

    private List<ComboItem> listCombo(String sql) throws SQLException {
        List<ComboItem> items = new ArrayList<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new ComboItem(rs.getLong(1), rs.getString(2)));
            }
        }
        return items;
    }
}
