package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class FindingsDao {
    private static final String SQL_FINDINGS = """
            SELECT tipo, total, detalle, accion_sugerida
            FROM (
                SELECT 'Vacios' AS tipo,
                       COUNT(*) AS total,
                       'Bitacoras rechazadas o pendientes de validacion' AS detalle,
                       'Refuerzo de plantilla y retroalimentacion semanal' AS accion_sugerida
                FROM bitacora
                WHERE estado IN ('RECHAZADA', 'PENDIENTE')

                UNION ALL

                SELECT 'Tensiones' AS tipo,
                       COUNT(*) AS total,
                       'Practicas en curso cercanas a meta de horas sin cierre' AS detalle,
                       'Comite de seguimiento para cierres priorizados' AS accion_sugerida
                FROM practica
                WHERE estado = 'EN_CURSO'
                  AND horas_validadas >= (horas_objetivo * 0.80)

                UNION ALL

                SELECT 'Fortalezas' AS tipo,
                       COUNT(*) AS total,
                       'Practicas finalizadas con notas altas' AS detalle,
                       'Socializar casos de exito por programa' AS accion_sugerida
                FROM evaluacion
                WHERE nota_global >= 4.5
            )
            ORDER BY tipo
            """;

    private static final String SQL_INSERT_ACTION = """
            INSERT INTO hallazgo_mejora (
                id_hallazgo, tipo_hallazgo, accion_mejora, id_director, fecha_registro
            )
            VALUES (
                (SELECT NVL(MAX(id_hallazgo), 0) + 1 FROM hallazgo_mejora),
                ?, ?, ?, SYSTIMESTAMP
            )
            """;

    public DefaultTableModel listFindings() throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Tipo", "Total", "Detalle", "Accion sugerida"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_FINDINGS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("tipo"),
                        rs.getString("total"),
                        rs.getString("detalle"),
                        rs.getString("accion_sugerida")
                });
            }
        }
        return model;
    }

    public void registerImprovementAction(String tipo, String accion, long directorId) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT_ACTION)) {
            ps.setString(1, tipo);
            ps.setString(2, accion);
            ps.setLong(3, directorId);
            ps.executeUpdate();
        }
    }
}
