package co.udi.integrador.ui.launchers;

import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import co.udi.integrador.session.RunMode;
import co.udi.integrador.ui.BitacoraFrame;
import co.udi.integrador.ui.DirectorApprovalFrame;
import co.udi.integrador.ui.DirectorDashboardFrame;
import co.udi.integrador.ui.EvidenceFrame;
import co.udi.integrador.ui.AppIconFactory;
import co.udi.integrador.ui.LoginFrame;
import co.udi.integrador.ui.PracticeRegistrationFrame;
import co.udi.integrador.ui.ReportsFrame;
import co.udi.integrador.ui.StudentDashboardFrame;
import co.udi.integrador.ui.StudentSelfRegistrationFrame;
import co.udi.integrador.ui.TeacherDashboardFrame;
import co.udi.integrador.ui.TeacherRegistrationFrame;
import co.udi.integrador.ui.ValidationFrame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class AbrirMenuVistas extends JFrame {
    public AbrirMenuVistas() {
        super(AppIconFactory.windowTitle("Menu de Vistas - NetBeans"));
        setIconImage(AppIconFactory.appIcon());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 500);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        JLabel title = new JLabel(AppIconFactory.APP_NAME + " - Abrir vistas una a una", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        root.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        root.add(grid, BorderLayout.CENTER);

        grid.add(button("Login", () -> openNoSession(LoginFrame::new)));
        grid.add(button("Autoregistro estudiante", () -> openNoSession(StudentSelfRegistrationFrame::new)));
        grid.add(button("Dashboard estudiante", () -> openAs(Role.ESTUDIANTE, StudentDashboardFrame::new)));
        grid.add(button("Asignaciones directora", () -> openAs(Role.DIRECTOR, () -> new PracticeRegistrationFrame(Role.DIRECTOR))));
        grid.add(button("Bitacora", () -> openAs(Role.ESTUDIANTE, () -> new BitacoraFrame(Role.ESTUDIANTE))));
        grid.add(button("Evidencias", () -> openAs(Role.ESTUDIANTE, () -> new EvidenceFrame(Role.ESTUDIANTE))));
        grid.add(button("Dashboard docente", () -> openAs(Role.DOCENTE, TeacherDashboardFrame::new)));
        grid.add(button("Validacion docente", () -> openAs(Role.DOCENTE, ValidationFrame::new)));
        grid.add(button("Dashboard directora", () -> openAs(Role.DIRECTOR, DirectorDashboardFrame::new)));
        grid.add(button("Aprobaciones directora", () -> openAs(Role.DIRECTOR, DirectorApprovalFrame::new)));
        grid.add(button("Registro docente", () -> openAs(Role.DIRECTOR, TeacherRegistrationFrame::new)));
        grid.add(button("Reportes", () -> openAs(Role.DIRECTOR, ReportsFrame::new)));
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        return b;
    }

    private void openNoSession(Supplier<JFrame> supplier) {
        AppSession.clear();
        supplier.get().setVisible(true);
    }

    private void openAs(Role role, Supplier<JFrame> supplier) {
        AppSession.setRunMode(RunMode.DEMO);
        AppSession.setCurrentUser(buildDemoUser(role));
        supplier.get().setVisible(true);
    }

    private AuthenticatedUser buildDemoUser(Role role) {
        if (role == Role.ESTUDIANTE) {
            return new AuthenticatedUser(101L, "Estudiante Demo", "estudiante.demo@udi.edu.co", Role.ESTUDIANTE);
        }
        if (role == Role.DOCENTE) {
            return new AuthenticatedUser(202L, "Docente Demo", "docente.demo@udi.edu.co", Role.DOCENTE);
        }
        return new AuthenticatedUser(303L, "Directora Demo", "directora.demo@udi.edu.co", Role.DIRECTOR);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AbrirMenuVistas().setVisible(true));
    }
}
