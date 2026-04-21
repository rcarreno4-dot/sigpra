package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class DirectorApprovalDao {
    private static final String SQL_PENDING = """
            SELECT p.id_practica,
                   TRIM(ue.nombres || ' ' || ue.apellidos) AS estudiante,
                   TRIM(ud.nombres || ' ' || ud.apellidos) AS docente,
                   p.periodo,
                   TO_CHAR(NVL(SUM(CASE WHEN b.estado_validacion = 'VALIDADA' THEN b.horas_reportadas ELSE 0 END), 0))
                       || '/' || TO_CHAR(p.horas_objetivo) AS avance_horas,
                   SUM(CASE WHEN b.estado_validacion = 'VALIDADA' THEN 1 ELSE 0 END) AS actividades_validadas,
                   SUM(CASE WHEN b.estado_validacion = 'RECHAZADA' THEN 1 ELSE 0 END) AS actividades_rechazadas,
                   SUM(CASE WHEN b.estado_validacion = 'PENDIENTE' THEN 1 ELSE 0 END) AS actividades_pendientes,
                   NVL(
                       MAX(
                           CASE
                               WHEN b.observacion_docente IS NOT NULL AND TRIM(b.observacion_docente) <> ''
                                   THEN b.observacion_docente
                               ELSE NULL
                           END
                       ) KEEP (DENSE_RANK LAST ORDER BY b.fecha_actividad, b.id_bitacora),
                       'Sin observacion'
                   ) AS observacion_docente,
                   TO_CHAR(NVL(h.ultima_actividad, p.fecha_fin), 'YYYY-MM-DD') AS fecha_registro
            FROM practica p
            JOIN estudiante s ON s.id_estudiante = p.id_estudiante
            JOIN usuario ue ON ue.id_usuario = s.id_usuario
            JOIN docente_asesor da ON da.id_docente = p.id_docente
            JOIN usuario ud ON ud.id_usuario = da.id_usuario
            JOIN director_programa dp ON dp.programa = s.programa
            LEFT JOIN (
                SELECT b.id_practica,
                       MAX(b.fecha_actividad) AS ultima_actividad
                FROM bitacora b
                GROUP BY b.id_practica
            ) h ON h.id_practica = p.id_practica
            LEFT JOIN bitacora b ON b.id_practica = p.id_practica
            WHERE p.estado = 'PENDIENTE_APROBACION'
              AND dp.id_usuario = ?
            GROUP BY p.id_practica,
                     ue.nombres, ue.apellidos,
                     ud.nombres, ud.apellidos,
                     p.periodo, p.horas_objetivo,
                     h.ultima_actividad, p.fecha_fin
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
                new String[]{
                        "ID Practica",
                        "Estudiante",
                        "Docente",
                        "Periodo",
                        "Horas",
                        "Validadas",
                        "Rechazadas",
                        "Pendientes",
                        "Observacion docente",
                        "Fecha registro"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_PENDING)) {
            ps.setLong(1, directorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getLong("id_practica"),
                            rs.getString("estudiante"),
                            rs.getString("docente"),
                            rs.getString("periodo"),
                            rs.getString("avance_horas"),
                            rs.getInt("actividades_validadas"),
                            rs.getInt("actividades_rechazadas"),
                            rs.getInt("actividades_pendientes"),
                            rs.getString("observacion_docente"),
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
