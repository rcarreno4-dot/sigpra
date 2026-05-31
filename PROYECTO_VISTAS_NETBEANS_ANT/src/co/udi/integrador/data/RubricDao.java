package co.udi.integrador.data;

import co.udi.integrador.model.ComboItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class RubricDao {
    private static final String SQL_PRACTICES = """
            SELECT p.id_practica,
                   TRIM(u.nombres || ' ' || u.apellidos) || ' - ' || p.periodo AS label
            FROM practica p
            JOIN estudiante e ON e.id_estudiante = p.id_estudiante
            JOIN usuario u ON u.id_usuario = e.id_usuario
            WHERE p.id_docente = ?
              AND p.estado IN ('EN_CURSO', 'PENDIENTE_APROBACION', 'FINALIZADA')
            ORDER BY p.id_practica DESC
            """;

    private static final String SQL_RECENT = """
            SELECT e.id_evaluacion,
                   p.id_practica,
                   TRIM(u.nombres || ' ' || u.apellidos) AS estudiante,
                   TO_CHAR(e.fecha_evaluacion, 'YYYY-MM-DD') AS fecha,
                   TO_CHAR(e.puntaje_total, 'FM990.00') AS nota,
                   e.estado
            FROM evaluacion e
            JOIN practica p ON p.id_practica = e.id_practica
            JOIN estudiante s ON s.id_estudiante = p.id_estudiante
            JOIN usuario u ON u.id_usuario = s.id_usuario
            WHERE e.id_docente = ?
            ORDER BY e.fecha_evaluacion DESC, e.id_evaluacion DESC
            """;

    private static final String SQL_INSERT_EVAL = """
            INSERT INTO evaluacion (
                id_evaluacion, id_practica, id_docente, fecha_evaluacion,
                puntaje_total, observaciones, estado, id_rubrica
            )
            VALUES (
                (SELECT NVL(MAX(id_evaluacion), 0) + 1 FROM evaluacion),
                ?, ?, SYSTIMESTAMP,
                ?, ?, 'EN_REVISION',
                (SELECT id_rubrica FROM rubrica WHERE estado = 'ACTIVA' AND ROWNUM = 1)
            )
            """;

    public List<ComboItem> listPracticesForTeacher(long teacherId) throws SQLException {
        List<ComboItem> list = new ArrayList<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_PRACTICES)) {
            ps.setLong(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ComboItem(rs.getLong("id_practica"), rs.getString("label")));
                }
            }
        }
        return list;
    }

    public DefaultTableModel listRecentEvaluations(long teacherId) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID Eval", "ID Practica", "Estudiante", "Fecha", "Nota", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_RECENT)) {
            ps.setLong(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("id_evaluacion"),
                            rs.getString("id_practica"),
                            rs.getString("estudiante"),
                            rs.getString("fecha"),
                            rs.getString("nota"),
                            rs.getString("estado")
                    });
                }
            }
        }
        return model;
    }

    public void saveEvaluation(
            long practiceId,
            long teacherId,
            int planeacion,
            int ejecucion,
            int reflexion,
            int evidencias,
            String comentario) throws SQLException {
        double avg = (planeacion + ejecucion + reflexion + evidencias) / 4.0d;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT_EVAL)) {
            ps.setLong(1, practiceId);
            ps.setLong(2, teacherId);
            ps.setDouble(3, avg);
            ps.setString(4, comentario);
            ps.executeUpdate();
        }
    }
}
