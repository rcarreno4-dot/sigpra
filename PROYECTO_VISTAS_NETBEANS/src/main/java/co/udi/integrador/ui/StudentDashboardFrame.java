package co.udi.integrador.ui;

import co.udi.integrador.data.DashboardDao;
import co.udi.integrador.data.DemoData;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class StudentDashboardFrame extends BaseFrame {
    public StudentDashboardFrame() {
        super("Panel de Estudiante", "Seguimiento de practica academica y bitacora personal", Role.ESTUDIANTE);
        buildNav();
        buildBody();
    }

    private void buildNav() {
        navButton("Dashboard", true, () -> { });
        navButton("Registro", false, () -> goTo(new PracticeRegistrationFrame(role)));
        navButton("Bitacora", false, () -> goTo(new BitacoraFrame(role)));
        navButton("Evidencias", false, () -> goTo(new EvidenceFrame(role)));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);

        DashboardDao.StudentDashboardData data = loadData();

        JPanel kpis = new JPanel(new GridLayout(1, 3, 10, 10));
        kpis.setOpaque(false);
        kpis.add(kpiCard("Horas registradas", data.horasRegistradas()));
        kpis.add(kpiCard("Horas validadas", data.horasValidadas()));
        kpis.add(kpiCard("Estado practica", data.estadoPractica()));

        JTable table = new JTable(data.tasks());
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Proximas tareas"), BorderLayout.NORTH);
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

    private DashboardDao.StudentDashboardData loadData() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            return new DashboardDao.StudentDashboardData("126", "94", "En curso", DemoData.studentTasksModel());
        }
        try {
            return new DashboardDao().loadStudentData(user.id());
        } catch (SQLException ex) {
            return new DashboardDao.StudentDashboardData("126", "94", "En curso", DemoData.studentTasksModel());
        }
    }
}
