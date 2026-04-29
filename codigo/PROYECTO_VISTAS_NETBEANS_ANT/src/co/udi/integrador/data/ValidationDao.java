package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class ValidationDao {
    private static final String SQL_PENDING = """
            SELECT b.id_bitacora,
                   TRIM(u.nombres || ' ' || u.apellidos) AS estudiante,
                   TO_CHAR(b.fecha_actividad, 'YYYY-MM-DD') AS fecha,
                   b.actividad,
                   TO_CHAR(b.horas_reportadas) AS horas,
                   INITCAP(LOWER(b.estado_validacion)) AS estado
            FROM bitacora b
            JOIN practica p ON p.id_practica = b.id_practica
            JOIN estudiante e ON e.id_estudiante = p.id_estudiante
            JOIN usuario u ON u.id_usuario = e.id_usuario
            WHERE p.id_docente = ?
            ORDER BY CASE WHEN b.estado_validacion = 'PENDIENTE' THEN 0 ELSE 1 END,
                     b.fecha_actividad DESC,
                     b.id_bitacora DESC
            """;

    private static final String SQL_UPDATE_VALIDATION = """
            UPDATE bitacora
            SET estado_validacion = ?,
                observacion_docente = ?,
                id_docente_validador = ?,
                fecha_validacion = SYSTIMESTAMP
            WHERE id_bitacora = ?
            """;

    private static final String SQL_FIND_PRACTICE_BY_BITACORA = """
            SELECT id_practica
            FROM bitacora
            WHERE id_bitacora = ?
            """;

    private static final String SQL_RECALC_HOURS = """
            UPDATE practica p
            SET horas_acumuladas = (
                SELECT NVL(SUM(b.horas_reportadas), 0)
                FROM bitacora b
                WHERE b.id_practica = p.id_practica
                  AND b.estado_validacion = 'VALIDADA'
            )
            WHERE p.id_practica = ?
            """;

    private static final String SQL_UPDATE_PRACTICE_STATUS = """
            UPDATE practica
            SET estado = CASE
                WHEN horas_acumuladas >= horas_objetivo THEN 'PENDIENTE_APROBACION'
                WHEN horas_acumuladas < horas_objetivo AND estado IN ('PENDIENTE_APROBACION', 'FINALIZADA') THEN 'EN_CURSO'
                WHEN estado = 'PENDIENTE' THEN 'EN_CURSO'
                ELSE estado
            END
            WHERE id_practica = ?
            """;

    public DefaultTableModel listForTeacher(long teacherId) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Estudiante", "Fecha", "Actividad", "Horas", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_PENDING)) {
            ps.setLong(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getLong("id_bitacora"),
                            rs.getString("estudiante"),
                            rs.getString("fecha"),
                            rs.getString("actividad"),
                            rs.getString("horas"),
                            rs.getString("estado")
                    });
                }
            }
        }
        return model;
    }

    public void validate(long bitacoraId, long teacherId, boolean approved, String observation) throws SQLException {
        String newStatus = approved ? "VALIDADA" : "RECHAZADA";

        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(SQL_UPDATE_VALIDATION)) {
                    ps.setString(1, newStatus);
                    ps.setString(2, observation);
                    ps.setLong(3, teacherId);
                    ps.setLong(4, bitacoraId);
                    ps.executeUpdate();
                }

                long practiceId = findPracticeId(cn, bitacoraId);
                if (practiceId > 0) {
                    try (PreparedStatement ps = cn.prepareStatement(SQL_RECALC_HOURS)) {
                        ps.setLong(1, practiceId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = cn.prepareStatement(SQL_UPDATE_PRACTICE_STATUS)) {
                        ps.setLong(1, practiceId);
                        ps.executeUpdate();
                    }
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

    private long findPracticeId(Connection cn, long bitacoraId) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_FIND_PRACTICE_BY_BITACORA)) {
            ps.setLong(1, bitacoraId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("id_practica") : -1;
            }
        }
    }
}
