package co.udi.integrador.data;

import co.udi.integrador.model.ComboItem;
import co.udi.integrador.model.BitacoraEntry;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public final class DemoData {
    private DemoData() {
    }

    public static DefaultTableModel studentTasksModel() {
        String[] columns = {"Tarea", "Fecha limite", "Estado"};
        Object[][] rows = {
            {"Subir evidencia semana 6", "2026-03-26", "Pendiente"},
            {"Actualizar bitacora de actividad", "2026-03-27", "Atrasada"},
            {"Revision parcial con docente", "2026-03-29", "Programada"}
        };
        return model(columns, rows);
    }

    public static DefaultTableModel teacherQueueModel() {
        String[] columns = {"Estudiante", "Actividad", "Horas", "Estado"};
        Object[][] rows = {
            {"Fabian Carreno", "Planeacion de clase", "4", "Pendiente"},
            {"Nicolas Marino", "Aplicacion de instrumento", "3", "Pendiente"},
            {"Santiago Rojas", "Sesion de refuerzo", "2", "Validada"}
        };
        return model(columns, rows);
    }

    public static DefaultTableModel directorStatusModel() {
        String[] columns = {"Programa", "Pend. aprobacion", "En curso", "Finalizadas"};
        Object[][] rows = {
            {"Lic. Ingles", "2", "15", "7"},
            {"Lic. Matematicas", "3", "13", "3"},
            {"Lic. Pedagogia Infantil", "1", "13", "5"}
        };
        return model(columns, rows);
    }

    public static DefaultTableModel bitacoraModel() {
        String[] columns = {"Fecha", "Actividad", "Horas", "Estado"};
        Object[][] rows = {
            {"2026-03-15", "Observacion de aula", "4", "Validada"},
            {"2026-03-16", "Diseno de taller", "3", "Pendiente"},
            {"2026-03-17", "Aplicacion de estrategia", "5", "Rechazada"}
        };
        return model(columns, rows);
    }

    public static List<BitacoraEntry> bitacoraEntries() {
        return List.of(
            new BitacoraEntry("2026-03-15", "Observacion de aula", "4", "Validada"),
            new BitacoraEntry("2026-03-16", "Diseno de taller", "3", "Pendiente"),
            new BitacoraEntry("2026-03-17", "Aplicacion de estrategia", "5", "Rechazada")
        );
    }

    public static DefaultTableModel reportsModel() {
        String[] columns = {"Programa", "Total", "Pendientes", "En curso", "Pend. aprobacion", "Finalizadas"};
        Object[][] rows = {
            {"Lic. Ingles", "24", "1", "14", "2", "7"},
            {"Lic. Matematicas", "19", "1", "12", "3", "3"},
            {"Lic. Pedagogia Infantil", "19", "0", "13", "1", "5"}
        };
        return model(columns, rows);
    }

    public static DefaultTableModel validationModel() {
        String[] columns = {"Estudiante", "Fecha", "Actividad", "Horas", "Estado"};
        Object[][] rows = {
            {"Fabian Carreno", "2026-03-16", "Diseno de taller de lectura", "3", "Pendiente"},
            {"Nicolas Marino", "2026-03-16", "Apoyo en evaluacion diagnostica", "2", "Pendiente"}
        };
        return model(columns, rows);
    }

    public static DefaultTableModel templateConfigModel() {
        String[] columns = {"ID", "Periodo", "Modalidad", "Plantilla", "Estado", "Version"};
        Object[][] rows = {
            {"1", "2026-1", "Presencial", "Plantilla base semanal", "ACTIVA", "1"},
            {"2", "2026-1", "Virtual", "Plantilla virtual por resultados", "ACTIVA", "2"}
        };
        return model(columns, rows);
    }

    public static List<ComboItem> rubricPractices() {
        return List.of(
            new ComboItem(301L, "Practica #301 - Estudiante Demo - 2026-1"),
            new ComboItem(302L, "Practica #302 - Estudiante Demo - 2026-1")
        );
    }

    public static DefaultTableModel rubricEvaluationModel() {
        String[] columns = {"ID Eval", "ID Practica", "Estudiante", "Fecha", "Nota", "Estado"};
        Object[][] rows = {
            {"7001", "301", "Fabian Carreno", "2026-03-18", "4.40", "REGISTRADA"},
            {"7002", "302", "Nicolas Marino", "2026-03-20", "4.70", "REGISTRADA"}
        };
        return model(columns, rows);
    }

    public static DefaultTableModel findingsModel() {
        String[] columns = {"Tipo", "Total", "Detalle", "Accion sugerida"};
        Object[][] rows = {
            {"Vacios", "4", "Bitacoras con registros incompletos", "Refuerzo de plantilla y guia docente"},
            {"Tensiones", "3", "Practicas en curso cercanas al limite sin cierre", "Mesa semanal de seguimiento"},
            {"Fortalezas", "9", "Practicas finalizadas con nota >= 4.5", "Socializar buenas practicas"}
        };
        return model(columns, rows);
    }

    private static DefaultTableModel model(String[] columns, Object[][] rows) {
        return new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
