package co.udi.integrador.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ReportsDao {
    private static final String SQL_PERIODS = """
            SELECT DISTINCT periodo
            FROM practica
            ORDER BY periodo DESC
            """;

    private static final String SQL_PROGRAMS = """
            SELECT DISTINCT programa
            FROM estudiante
            ORDER BY programa
            """;

    public List<String> listPeriods() throws SQLException {
        return listValues(SQL_PERIODS, "periodo");
    }

    public List<String> listPrograms() throws SQLException {
        return listValues(SQL_PROGRAMS, "programa");
    }

    public DefaultTableModel queryReport(String periodo, String programa, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT e.programa,
                       COUNT(*) AS total_practicas,
                       NVL(SUM(p.horas_acumuladas), 0) AS horas_acumuladas,
                       NVL(SUM(p.horas_objetivo), 0) AS horas_objetivo,
                       NVL(SUM(CASE WHEN p.estado = 'PENDIENTE' THEN 1 ELSE 0 END), 0) AS pendientes,
                       NVL(SUM(CASE WHEN p.estado = 'EN_CURSO' THEN 1 ELSE 0 END), 0) AS en_curso,
                       NVL(SUM(CASE WHEN p.estado = 'PENDIENTE_APROBACION' THEN 1 ELSE 0 END), 0) AS pendientes_aprobacion,
                       NVL(SUM(CASE WHEN p.estado = 'FINALIZADA' THEN 1 ELSE 0 END), 0) AS finalizadas
                FROM practica p
                JOIN estudiante e ON e.id_estudiante = p.id_estudiante
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();
        if (periodo != null && !periodo.equalsIgnoreCase("Todos")) {
            sql.append(" AND p.periodo = ? ");
            params.add(periodo);
        }
        if (programa != null && !programa.equalsIgnoreCase("Todos")) {
            sql.append(" AND e.programa = ? ");
            params.add(programa);
        }
        if (estado != null && !estado.equalsIgnoreCase("Todos")) {
            sql.append(" AND p.estado = ? ");
            params.add(mapEstado(estado));
        }

        sql.append(" GROUP BY e.programa ORDER BY e.programa ");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Programa", "Total", "Horas acumuladas", "Horas objetivo", "Pendientes", "En curso", "Pend. aprobacion", "Finalizadas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("programa"),
                            rs.getString("total_practicas"),
                            rs.getString("horas_acumuladas"),
                            rs.getString("horas_objetivo"),
                            rs.getString("pendientes"),
                            rs.getString("en_curso"),
                            rs.getString("pendientes_aprobacion"),
                            rs.getString("finalizadas"),
                    });
                }
            }
        }
        return model;
    }

    private List<String> listValues(String sql, String col) throws SQLException {
        List<String> values = new ArrayList<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getString(col));
            }
        }
        return values;
    }

    private String mapEstado(String estadoUi) {
        return switch (estadoUi) {
            case "Pendiente" -> "PENDIENTE";
            case "En curso" -> "EN_CURSO";
            case "Pend. aprobacion" -> "PENDIENTE_APROBACION";
            case "Finalizada" -> "FINALIZADA";
            default -> estadoUi;
        };
    }
}
