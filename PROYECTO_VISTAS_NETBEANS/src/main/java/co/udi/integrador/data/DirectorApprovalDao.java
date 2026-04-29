package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class DirectorApprovalDao {
    private static final String SQL_PENDING = """
            SELECT p.id_practica,
                   TRIM(u.nombres || ' ' || u.apellidos) AS estudiante,
                   p.periodo,
                   TO_CHAR(NVL(h.horas_validas, 0)) || '/' || TO_CHAR(p.horas_objetivo) AS avance_horas,
                   TO_CHAR(NVL(h.ultima_actividad, p.fecha_fin), 'YYYY-MM-DD') AS fecha_registro
            FROM practica p
            JOIN estudiante s ON s.id_estudiante = p.id_estudiante
            JOIN usuario u ON u.id_usuario = s.id_usuario
            LEFT JOIN (
                SELECT b.id_practica,
                       NVL(SUM(CASE WHEN b.estado_validacion = 'VALIDADA' THEN b.horas_reportadas ELSE 0 END), 0) AS horas_validas,
                       MAX(b.fecha_actividad) AS ultima_actividad
                FROM bitacora b
                GROUP BY b.id_practica
            ) h ON h.id_practica = p.id_practica
            WHERE p.estado = 'PENDIENTE_APROBACION'
            ORDER BY fecha_registro DESC, p.id_practica DESC
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

    public DefaultTableModel listPendingApprovals(long directorId) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID Practica", "Estudiante", "Periodo", "Horas", "Fecha registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_PENDING)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getLong("id_practica"),
                            rs.getString("estudiante"),
                            rs.getString("periodo"),
                            rs.getString("avance_horas"),
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
