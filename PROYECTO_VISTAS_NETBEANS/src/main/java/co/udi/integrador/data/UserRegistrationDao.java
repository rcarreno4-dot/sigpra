package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserRegistrationDao {
    private static final String SQL_INSERT_USER = """
            INSERT INTO usuario (
                id_usuario, nombres, apellidos, correo, identificacion, hash_password, rol, estado
            ) VALUES (
                NULL, ?, ?, ?, ?, ?, ?, 'ACTIVO'
            )
            """;

    private static final String SQL_FIND_USER_BY_EMAIL = """
            SELECT id_usuario
            FROM usuario
            WHERE LOWER(correo) = LOWER(?)
            """;

    private static final String SQL_INSERT_STUDENT = """
            INSERT INTO estudiante (id_estudiante, codigo, programa, semestre, id_usuario)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String SQL_INSERT_TEACHER = """
            INSERT INTO docente_asesor (id_docente, especialidad, id_usuario)
            VALUES (?, ?, ?)
            """;

    public void registerStudent(
            String fullName,
            String email,
            String password,
            String studentCode,
            String program,
            Integer semester
    ) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setAutoCommit(false);
            try {
                long userId = createUser(cn, fullName, email, password, "ESTUDIANTE", studentCode);
                try (PreparedStatement ps = cn.prepareStatement(SQL_INSERT_STUDENT)) {
                    ps.setLong(1, userId);
                    ps.setString(2, studentCode);
                    ps.setString(3, program);
                    if (semester == null) {
                        ps.setNull(4, Types.NUMERIC);
                    } else {
                        ps.setInt(4, semester);
                    }
                    ps.setLong(5, userId);
                    ps.executeUpdate();
                }
                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    public void registerTeacher(String fullName, String email, String password, String area) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setAutoCommit(false);
            try {
                long userId = createUser(cn, fullName, email, password, "DOCENTE", "DOC-" + Math.abs(email.hashCode()));
                try (PreparedStatement ps = cn.prepareStatement(SQL_INSERT_TEACHER)) {
                    ps.setLong(1, userId);
                    ps.setString(2, area);
                    ps.setLong(3, userId);
                    ps.executeUpdate();
                }
                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    private long createUser(
            Connection cn,
            String fullName,
            String email,
            String password,
            String role,
            String identificationBase
    ) throws SQLException {
        NameParts name = splitFullName(fullName);
        String identificacion = buildIdentification(identificationBase, email);

        try (PreparedStatement ps = cn.prepareStatement(SQL_INSERT_USER)) {
            ps.setString(1, name.nombres());
            ps.setString(2, name.apellidos());
            ps.setString(3, email);
            ps.setString(4, identificacion);
            ps.setString(5, password);
            ps.setString(6, role);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = cn.prepareStatement(SQL_FIND_USER_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No fue posible recuperar el usuario creado.");
                }
                return rs.getLong("id_usuario");
            }
        }
    }

    private NameParts splitFullName(String fullName) {
        String safe = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        if (safe.isBlank()) {
            return new NameParts("Estudiante", "SIGPRA");
        }
        int cut = safe.lastIndexOf(' ');
        if (cut <= 0) {
            return new NameParts(safe, "SIGPRA");
        }
        String nombres = safe.substring(0, cut).trim();
        String apellidos = safe.substring(cut + 1).trim();
        if (nombres.isBlank()) {
            nombres = "Estudiante";
        }
        if (apellidos.isBlank()) {
            apellidos = "SIGPRA";
        }
        return new NameParts(nombres, apellidos);
    }

    private String buildIdentification(String base, String email) {
        String candidate = base == null ? "" : base.replaceAll("[^0-9A-Za-z]", "");
        if (candidate.isBlank()) {
            candidate = "ID" + Math.abs((email == null ? "" : email).hashCode());
        }
        if (candidate.length() > 30) {
            candidate = candidate.substring(0, 30);
        }
        return candidate;
    }

    private record NameParts(String nombres, String apellidos) {
    }
}
