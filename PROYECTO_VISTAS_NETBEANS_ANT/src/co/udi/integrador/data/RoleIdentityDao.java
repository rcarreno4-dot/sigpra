package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleIdentityDao {
    private static final String SQL_STUDENT_ID_BY_USER = """
            SELECT id_estudiante
            FROM estudiante
            WHERE id_usuario = ?
            """;

    private static final String SQL_TEACHER_ID_BY_USER = """
            SELECT id_docente
            FROM docente_asesor
            WHERE id_usuario = ?
            """;

    public long requireStudentIdByUserId(long userId) throws SQLException {
        return requireIdByUser(SQL_STUDENT_ID_BY_USER, userId, "estudiante");
    }

    public long requireTeacherIdByUserId(long userId) throws SQLException {
        return requireIdByUser(SQL_TEACHER_ID_BY_USER, userId, "docente");
    }

    public long requireDirectorIdByUserId(long userId) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            String directorTable = DbSchemaHelper.resolveDirectorTable(cn);
            String sql = """
                    SELECT id_director
                    FROM %s
                    WHERE id_usuario = ?
                    """.formatted(directorTable);
            return requireIdByUser(cn, sql, userId, "director");
        }
    }

    private long requireIdByUser(String sql, long userId, String roleLabel) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            return requireIdByUser(cn, sql, userId, roleLabel);
        }
    }

    private long requireIdByUser(Connection cn, String sql, long userId, String roleLabel) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("El usuario autenticado no tiene perfil de " + roleLabel + " asociado.");
                }
                return rs.getLong(1);
            }
        }
    }
}
