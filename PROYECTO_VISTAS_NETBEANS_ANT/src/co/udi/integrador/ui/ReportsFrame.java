package co.udi.integrador.ui;

import co.udi.integrador.data.ReportsDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReportsFrame extends BaseFrame {
    private final ReportsDao reportsDao = new ReportsDao();
    private JComboBox<String> cmbPeriodo;
    private JComboBox<String> cmbPrograma;
    private JComboBox<String> cmbEstado;
    private JTable table;

    public ReportsFrame() {
        super("Reportes", "Consolidado de avance de horas y estado operativo", Role.DIRECTOR);
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "Solo la directora puede consultar reportes institucionales.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            closeAllAndReturnToLogin();
            return;
        }
        buildNav();
        buildBody();
        loadFilters();
        queryReport();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Docentes", false, () -> goTo(new TeacherRegistrationFrame()));
        navButton("Asignaciones", false, () -> goTo(new PracticeRegistrationFrame(role)));
        navButton("Reportes", true, () -> { });
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        JPanel filters = UITheme.cardPanel();
        filters.setLayout(new GridLayout(2, 3, 8, 8));
        filters.add(UITheme.bodyLabel("Periodo"));
        filters.add(UITheme.bodyLabel("Programa"));
        filters.add(UITheme.bodyLabel("Estado"));

        cmbPeriodo = new JComboBox<>(new String[]{"Todos"});
        cmbPrograma = new JComboBox<>(new String[]{"Todos"});
        cmbEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "En curso", "Pend. aprobacion", "Finalizada"});
        filters.add(cmbPeriodo);
        filters.add(cmbPrograma);
        filters.add(cmbEstado);

        table = new JTable();
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Consolidado"), BorderLayout.NORTH);
        tableCard.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnQuery = UITheme.primaryButton("Consultar");
        btnQuery.addActionListener(e -> queryReport());
        actions.add(btnQuery);
        actions.add(UITheme.secondaryButton("Exportar PDF"));
        actions.add(UITheme.secondaryButton("Exportar Excel"));

        root.add(filters, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        body.add(root, BorderLayout.CENTER);
    }

    private void loadFilters() {
        try {
            for (String p : reportsDao.listPeriods()) {
                cmbPeriodo.addItem(p);
            }
            for (String p : reportsDao.listPrograms()) {
                cmbPrograma.addItem(p);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar filtros de reportes.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void queryReport() {
        String periodo = (String) cmbPeriodo.getSelectedItem();
        String programa = (String) cmbPrograma.getSelectedItem();
        String estado = (String) cmbEstado.getSelectedItem();
        try {
            table.setModel(reportsDao.queryReport(periodo, programa, estado));
        } catch (SQLException ex) {
            table.setModel(new DefaultTableModel(
                    new String[]{"Programa", "Total", "Horas acumuladas", "Horas objetivo", "Pendientes", "En curso", "Pend. aprobacion", "Finalizadas"}, 0));
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar el reporte.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
