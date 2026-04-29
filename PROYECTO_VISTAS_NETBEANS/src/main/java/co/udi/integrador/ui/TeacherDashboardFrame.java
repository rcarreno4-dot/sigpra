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

public class TeacherDashboardFrame extends BaseFrame {
    public TeacherDashboardFrame() {
        super("Panel de Docente Asesor", "Validacion de actividades y seguimiento de practicas", Role.DOCENTE);
        buildNav();
        buildBody();
    }

    private void buildNav() {
        navButton("Dashboard", true, () -> { });
        navButton("Validar", false, () -> goTo(new ValidationFrame()));
        navButton("Rubrica", false, () -> goTo(new RubricEvaluationFrame()));
        navButton("Bitacora", false, () -> goTo(new BitacoraFrame(role)));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);

        DashboardDao.TeacherDashboardData data = loadData();

        JPanel kpis = new JPanel(new GridLayout(1, 3, 10, 10));
        kpis.setOpaque(false);
        kpis.add(kpiCard("Estudiantes asignados", data.estudiantesAsignados()));
        kpis.add(kpiCard("Entradas por validar", data.entradasPendientes()));
        kpis.add(kpiCard("Practicas en curso", data.practicasEnCurso()));

        JTable table = new JTable(data.queue());
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Cola de validacion"), BorderLayout.NORTH);
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

    private DashboardDao.TeacherDashboardData loadData() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            return new DashboardDao.TeacherDashboardData("18", "12", "9", DemoData.teacherQueueModel());
        }
        try {
            return new DashboardDao().loadTeacherData(user.id());
        } catch (SQLException ex) {
            return new DashboardDao.TeacherDashboardData("18", "12", "9", DemoData.teacherQueueModel());
        }
    }
}
