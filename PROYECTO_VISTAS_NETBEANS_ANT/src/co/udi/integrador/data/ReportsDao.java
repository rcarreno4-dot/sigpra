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
                SELECT TRIM(u.nombres || ' ' || u.apellidos) AS estudiante,
                       e.codigo AS codigo_estudiante,
                       e.programa,
                       p.periodo,
                       NVL(p.horas_acumuladas, 0) AS horas_acumuladas,
                       NVL(p.horas_objetivo, 0) AS horas_objetivo,
                       p.estado
                FROM practica p
                JOIN estudiante e ON e.id_estudiante = p.id_estudiante
                JOIN usuario u ON u.id_usuario = e.id_usuario
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

        sql.append(" ORDER BY u.nombres, u.apellidos, p.periodo DESC ");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Estudiante", "Codigo", "Carrera", "Periodo", "Horas", "Meta horas", "Estado"}, 0) {
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
                            rs.getString("estudiante"),
                            rs.getString("codigo_estudiante"),
                            rs.getString("programa"),
                            rs.getString("periodo"),
                            rs.getString("horas_acumuladas"),
                            rs.getString("horas_objetivo"),
                            rs.getString("estado"),
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
