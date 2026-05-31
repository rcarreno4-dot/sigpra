package co.udi.integrador.ui;

import co.udi.integrador.data.AuthDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import co.udi.integrador.session.RunMode;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        super(AppIconFactory.windowTitle("Inicio de sesion"));
        setIconImage(AppIconFactory.appIcon());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2, 16, 0));
        root.setBackground(UITheme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        root.add(buildContextPanel());
        root.add(buildLoginPanel());

        setContentPane(root);
    }

    private JPanel buildContextPanel() {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BRAND);

        JLabel logo = new JLabel(AppIconFactory.loginWordmarkIcon(220, 68));
        logo.setAlignmentX(0f);
        panel.add(logo);
        panel.add(new JLabel(" "));

        JLabel title = new JLabel(AppIconFactory.APP_NAME + " - Contexto del sistema");
        title.setForeground(java.awt.Color.WHITE);
        title.setFont(UITheme.TITLE_FONT.deriveFont(22f));
        panel.add(title);
        panel.add(new JLabel(" "));

        panel.add(whiteLabel("Plataforma para registrar, validar y hacer seguimiento de practicas."));
        panel.add(new JLabel(" "));
        panel.add(whiteLabel("- Registro publico: solo estudiantes."));
        panel.add(whiteLabel("- Estudiante: registra practica, bitacora y evidencias."));
        panel.add(whiteLabel("- Docente: valida actividades y horas."));
        panel.add(whiteLabel("- Directora: registra docentes y aprueba cierre final."));
        return panel;
    }

    private JLabel whiteLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT);
        label.setForeground(java.awt.Color.WHITE);
        return label;
    }

    private JPanel buildLoginPanel() {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BorderLayout(12, 12));

        JLabel title = UITheme.titleLabel("Iniciar sesion");
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(9, 1, 0, 8));
        form.setOpaque(false);

        JTextField correo = UITheme.textField();
        JPasswordField clave = new JPasswordField();
        clave.setFont(UITheme.BODY_FONT);
        JComboBox<Role> rol = new JComboBox<>(Role.values());
        rol.setFont(UITheme.BODY_FONT);
        JComboBox<RunMode> modo = new JComboBox<>(RunMode.values());
        modo.setFont(UITheme.BODY_FONT);

        form.add(UITheme.bodyLabel("Correo institucional"));
        form.add(correo);
        form.add(UITheme.bodyLabel("Contrasena"));
        form.add(clave);
        form.add(UITheme.bodyLabel("Rol"));
        form.add(rol);
        form.add(UITheme.bodyLabel("Modo de ejecucion"));
        form.add(modo);
        form.add(UITheme.subtitleLabel("Oracle valida usuario real. Demo permite usar el sistema sin BD."));

        JButton ingresar = UITheme.primaryButton("Ingresar");
        ingresar.addActionListener(e -> {
            String email = correo.getText().trim();
            String password = new String(clave.getPassword());
            Role selected = (Role) rol.getSelectedItem();
            RunMode runMode = (RunMode) modo.getSelectedItem();
            if (selected == null || runMode == null || email.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Completa correo, contrasena, rol y modo de ejecucion.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (runMode == RunMode.DEMO) {
                AppSession.setRunMode(RunMode.DEMO);
                AppSession.setCurrentUser(buildOfflineUser(email, selected));
                openDashboardByRole(selected);
                dispose();
                return;
            }

            AuthDao authDao = new AuthDao();
            try {
                AuthenticatedUser user = authDao.authenticate(email, password, selected).orElse(null);
                if (user == null) {
                    JOptionPane.showMessageDialog(this,
                            "Credenciales invalidas o rol incorrecto.",
                            "Acceso denegado",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                AppSession.setRunMode(RunMode.ORACLE);
                AppSession.setCurrentUser(user);
                openDashboardByRole(selected);
                dispose();
            } catch (SQLException ex) {
                if (isPortableDbError(ex)) {
                    int option = JOptionPane.showConfirmDialog(this,
                            "No fue posible conectar a Oracle en esta ejecucion portable.\n"
                                    + "Puedes ingresar en MODO DEMO con el rol seleccionado.\n\n"
                                    + "Deseas continuar en modo demo?",
                            "Conexion Oracle no disponible",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (option == JOptionPane.YES_OPTION) {
                        AppSession.setRunMode(RunMode.DEMO);
                        AppSession.setCurrentUser(buildOfflineUser(email, selected));
                        openDashboardByRole(selected);
                        dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "No fue posible conectar a Oracle.\nRevisa db.properties o variables DB_URL, DB_USER, DB_PASSWORD.\n\nDetalle: "
                                + ex.getMessage(),
                        "Error de conexion",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton registrarEstudiante = UITheme.secondaryButton("Registrarse como estudiante");
        registrarEstudiante.addActionListener(e -> {
            RunMode runMode = (RunMode) modo.getSelectedItem();
            AppSession.setRunMode(runMode == null ? RunMode.ORACLE : runMode);
            new StudentSelfRegistrationFrame().setVisible(true);
            dispose();
        });

        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 8));
        actions.setOpaque(false);
        actions.add(ingresar);
        actions.add(registrarEstudiante);

        panel.add(form, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void openDashboardByRole(Role role) {
        switch (role) {
            case ESTUDIANTE -> new StudentDashboardFrame().setVisible(true);
            case DOCENTE -> new TeacherDashboardFrame().setVisible(true);
            case DIRECTOR -> new DirectorDashboardFrame().setVisible(true);
        }
    }

    private boolean isPortableDbError(SQLException ex) {
        StringBuilder details = new StringBuilder();
        details.append(ex.getMessage() == null ? "" : ex.getMessage());
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            details.append(' ').append(ex.getCause().getMessage());
        }
        String msg = details.toString().toLowerCase(Locale.ROOT);
        return msg.contains("oracle jdbc")
                || msg.contains("no se encontro el driver")
                || msg.contains("classnotfoundexception")
                || msg.contains("no suitable driver")
                || msg.contains("ora-01017")
                || msg.contains("invalid username/password")
                || msg.contains("io error")
                || msg.contains("network adapter")
                || msg.contains("listener");
    }

    private AuthenticatedUser buildOfflineUser(String email, Role role) {
        String safeEmail = (email == null || email.isBlank())
                ? role.name().toLowerCase(Locale.ROOT) + ".demo@sigpra.local"
                : email.trim();

        String localPart = safeEmail.contains("@")
                ? safeEmail.substring(0, safeEmail.indexOf('@'))
                : safeEmail;
        String normalized = localPart.isBlank() ? "Usuario" : localPart.replace('.', ' ').replace('_', ' ');
        String displayName = Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1) + " (Demo)";
        long id = Math.abs(safeEmail.hashCode()) + 1000L;

        return new AuthenticatedUser(id, displayName, safeEmail, role);
    }
}
