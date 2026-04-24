package co.udi.integrador.ui;

import co.udi.integrador.data.DashboardDao;
import co.udi.integrador.data.DemoData;
import co.udi.integrador.model.Role;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class DirectorDashboardFrame extends BaseFrame {
    public DirectorDashboardFrame() {
        super("Panel de Director", "Supervision institucional y aprobacion final de cierre", Role.DIRECTOR);
        buildNav();
        buildBody();
    }

    private void buildNav() {
        navButton("Dashboard", true, () -> { });
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Docentes", false, () -> goTo(new TeacherRegistrationFrame()));
        navButton("Asignaciones", false, () -> goTo(new PracticeRegistrationFrame(role)));
        navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);

        DashboardDao.DirectorDashboardData data = loadData();

        JPanel kpis = new JPanel(new GridLayout(1, 3, 10, 10));
        kpis.setOpaque(false);
        kpis.add(kpiCard("Pend. aprobacion", data.pendientesAprobacion()));
        kpis.add(kpiCard("En curso", data.enCurso()));
        kpis.add(kpiCard("Finalizadas", data.finalizadas()));

        JTable table = new JTable(data.statusByProgram());
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Estado general por programa"), BorderLayout.NORTH);
        tableCard.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        root.add(kpis, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
        body.add(root, BorderLayout.CENTER);
    }

    private JPanel kpiCard(String label, String value) {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BorderLayout());
        JLabel l1 = UITheme.subtitleLabel(label);
        JLabel l2 = UITheme.titleLabel(value);
        panel.add(l1, BorderLayout.NORTH);
        panel.add(l2, BorderLayout.CENTER);
        return panel;
    }

    private DashboardDao.DirectorDashboardData loadData() {
        try {
            return new DashboardDao().loadDirectorData();
        } catch (SQLException ex) {
            return new DashboardDao.DirectorDashboardData("7", "34", "15", DemoData.directorStatusModel());
        }
    }
}
