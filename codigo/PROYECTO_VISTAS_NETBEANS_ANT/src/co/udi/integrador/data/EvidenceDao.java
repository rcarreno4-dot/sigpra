package co.udi.integrador.data;

import co.udi.integrador.model.ComboItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvidenceDao {
    private static final String SQL_STUDENT_BITACORA = """
            SELECT b.id_bitacora,
                   TO_CHAR(b.fecha_actividad, 'YYYY-MM-DD') || ' - ' || b.actividad AS label
            FROM bitacora b
            JOIN practica p ON p.id_practica = b.id_practica
            WHERE p.id_estudiante = ?
            ORDER BY b.fecha_actividad DESC, b.id_bitacora DESC
            """;

    private static final String SQL_INSERT_EVIDENCE = """
            INSERT INTO evidencia (
                id_evidencia, id_bitacora, tipo_archivo, nombre_archivo, ruta_archivo, comentario
            ) VALUES (NULL, ?, ?, ?, ?, ?)
            """;

    public List<ComboItem> listBitacoraForStudent(long studentId) throws SQLException {
        List<ComboItem> list = new ArrayList<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_STUDENT_BITACORA)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ComboItem(rs.getLong("id_bitacora"), rs.getString("label")));
                }
            }
        }
        return list;
    }

    public void insertEvidence(long bitacoraId, String tipo, String nombreArchivo, String ruta, String comentario)
            throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT_EVIDENCE)) {
            ps.setLong(1, bitacoraId);
            ps.setString(2, tipo);
            ps.setString(3, nombreArchivo);
            ps.setString(4, ruta);
            ps.setString(5, comentario);
            ps.executeUpdate();
        }
    }
}
