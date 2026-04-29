package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class TemplateDao {
    private static final String SQL_LIST = """
            SELECT id_plantilla,
                   periodo,
                   modalidad,
                   nombre_plantilla,
                   estado,
                   version
            FROM plantilla_bitacora
            ORDER BY periodo DESC, id_plantilla DESC
            """;

    private static final String SQL_INSERT = """
            INSERT INTO plantilla_bitacora (
                id_plantilla, periodo, modalidad, nombre_plantilla, descripcion,
                estado, version, id_director_crea, fecha_creacion
            )
            VALUES (
                (SELECT NVL(MAX(id_plantilla), 0) + 1 FROM plantilla_bitacora),
                ?, ?, ?, ?,
                'ACTIVA', 1, ?, SYSTIMESTAMP
            )
            """;

    public DefaultTableModel listTemplates() throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Periodo", "Modalidad", "Plantilla", "Estado", "Version"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_LIST);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id_plantilla"),
                        rs.getString("periodo"),
                        rs.getString("modalidad"),
                        rs.getString("nombre_plantilla"),
                        rs.getString("estado"),
                        rs.getString("version")
                });
            }
        }
        return model;
    }

    public void createTemplate(
            String periodo,
            String modalidad,
            String nombrePlantilla,
            String descripcion,
            long directorId) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, periodo);
            ps.setString(2, modalidad);
            ps.setString(3, nombrePlantilla);
            ps.setString(4, descripcion);
            ps.setLong(5, directorId);
            ps.executeUpdate();
        }
    }
}
