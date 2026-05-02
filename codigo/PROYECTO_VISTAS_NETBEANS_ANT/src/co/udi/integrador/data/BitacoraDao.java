package co.udi.integrador.data;

import co.udi.integrador.model.AuthenticatedUser;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;

public class BitacoraDao {
    private final RoleIdentityDao roleIdentityDao = new RoleIdentityDao();

    private static final String[] COLUMNS = {"Fecha", "Actividad", "Horas", "Estado"};

    private static final String SQL_STUDENT = """
            SELECT TO_CHAR(b.fecha_actividad, 'YYYY-MM-DD') AS fecha,
                   b.actividad,
                   TO_CHAR(b.horas_reportadas) AS horas,
                   INITCAP(LOWER(b.estado_validacion)) AS estado
            FROM bitacora b
            JOIN practica p ON p.id_practica = b.id_practica
            WHERE p.id_estudiante = ?
            ORDER BY b.fecha_actividad DESC, b.id_bitacora DESC
            """;

    private static final String SQL_TEACHER = """
            SELECT TO_CHAR(b.fecha_actividad, 'YYYY-MM-DD') AS fecha,
                   b.actividad,
                   TO_CHAR(b.horas_reportadas) AS horas,
                   INITCAP(LOWER(b.estado_validacion)) AS estado
            FROM bitacora b
            JOIN practica p ON p.id_practica = b.id_practica
            WHERE p.id_docente = ?
            ORDER BY b.fecha_actividad DESC, b.id_bitacora DESC
            """;

    private static final String SQL_DIRECTOR = """
            SELECT fecha, actividad, horas, estado
            FROM (
                SELECT TO_CHAR(b.fecha_actividad, 'YYYY-MM-DD') AS fecha,
                       b.actividad,
                       TO_CHAR(b.horas_reportadas) AS horas,
                       INITCAP(LOWER(b.estado_validacion)) AS estado
                FROM bitacora b
                ORDER BY b.fecha_actividad DESC, b.id_bitacora DESC
            )
            WHERE ROWNUM <= 100
            """;

    private static final String SQL_ACTIVE_PRACTICE = """
            SELECT id_practica
            FROM (
                SELECT id_practica
                FROM practica
                WHERE id_estudiante = ?
                  AND estado IN ('EN_CURSO', 'PENDIENTE')
                ORDER BY fecha_registro DESC
            )
            WHERE ROWNUM = 1
            """;

    private static final String SQL_INSERT_ENTRY = """
            INSERT INTO bitacora (
                id_practica,
                fecha_actividad,
                actividad,
                descripcion,
                horas_reportadas,
                estado_validacion
            ) VALUES (?, ?, ?, ?, ?, 'PENDIENTE')
            """;

    private static final String SQL_STATUS_SUMMARY = """
            SELECT estado_validacion, COUNT(*) AS total
            FROM bitacora
            GROUP BY estado_validacion
            """;

    public DefaultTableModel findByUser(AuthenticatedUser user) throws SQLException {
        return switch (user.role()) {
            case ESTUDIANTE -> queryWithId(SQL_STUDENT, roleIdentityDao.requireStudentIdByUserId(user.id()));
            case DOCENTE -> queryWithId(SQL_TEACHER, roleIdentityDao.requireTeacherIdByUserId(user.id()));
            case DIRECTOR -> queryNoParams(SQL_DIRECTOR);
        };
    }

    public void createStudentEntry(
            long studentUserId,
            LocalDate fechaActividad,
            String actividad,
            String descripcion,
            BigDecimal horasReportadas
    ) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection()) {
            long studentId = roleIdentityDao.requireStudentIdByUserId(studentUserId);
            long practiceId = findActivePracticeId(cn, studentId);
            if (practiceId <= 0) {
                throw new SQLException("No se encontro una practica activa para el estudiante.");
            }

            try (PreparedStatement ps = cn.prepareStatement(SQL_INSERT_ENTRY)) {
                ps.setLong(1, practiceId);
                ps.setDate(2, Date.valueOf(fechaActividad));
                ps.setString(3, actividad);
                ps.setString(4, descripcion);
                ps.setBigDecimal(5, horasReportadas);
                ps.executeUpdate();
            }
        }
    }

    private DefaultTableModel queryWithId(String sql, long id) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return toTable(rs);
            }
        }
    }

    private DefaultTableModel queryNoParams(String sql) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return toTable(rs);
        }
    }

    private DefaultTableModel toTable(ResultSet rs) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString("fecha"),
                    rs.getString("actividad"),
                    rs.getString("horas"),
                    rs.getString("estado")
            });
        }
        return model;
    }

    private long findActivePracticeId(Connection cn, long studentId) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_ACTIVE_PRACTICE)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }
                return rs.getLong("id_practica");
            }
        }
    }

    public int[] statusSummary() throws SQLException {
        int pending = 0;
        int valid = 0;
        int rejected = 0;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_STATUS_SUMMARY);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("estado_validacion");
                int total = rs.getInt("total");
                if ("PENDIENTE".equals(status)) {
                    pending = total;
                } else if ("VALIDADA".equals(status)) {
                    valid = total;
                } else if ("RECHAZADA".equals(status)) {
                    rejected = total;
                }
            }
        }
        return new int[]{pending, valid, rejected};
    }
}
