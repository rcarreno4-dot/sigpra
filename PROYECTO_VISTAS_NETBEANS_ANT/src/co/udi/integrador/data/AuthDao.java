package co.udi.integrador.data;

import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AuthDao {
    private static final String SQL_LOGIN = """
            SELECT id_usuario,
                   TRIM(nombres || ' ' || apellidos) AS nombre_completo,
                   correo,
                   rol
            FROM usuario
            WHERE LOWER(correo) = LOWER(?)
              AND hash_password = ?
              AND rol = ?
              AND estado = 'ACTIVO'
            """;

    public Optional<AuthenticatedUser> authenticate(String correo, String password, Role role) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_LOGIN)) {

            ps.setString(1, correo);
            ps.setString(2, password);
            ps.setString(3, role.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                AuthenticatedUser user = new AuthenticatedUser(
                        rs.getLong("id_usuario"),
                        rs.getString("nombre_completo"),
                        rs.getString("correo"),
                        Role.valueOf(rs.getString("rol"))
                );
                return Optional.of(user);
            }
        }
    }
}
