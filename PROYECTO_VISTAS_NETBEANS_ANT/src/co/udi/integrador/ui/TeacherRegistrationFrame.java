package co.udi.integrador.ui;

import co.udi.integrador.data.UserRegistrationDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class TeacherRegistrationFrame extends BaseFrame {
    private final UserRegistrationDao registrationDao = new UserRegistrationDao();

    public TeacherRegistrationFrame() {
        super("Registro de Docentes", "Administracion de docentes asesores por directora", Role.DIRECTOR);
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "Solo la directora puede registrar docentes.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            closeAllAndReturnToLogin();
            return;
        }
        buildNav();
        buildBody();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Docentes", true, () -> { });
        navButton("Asignaciones", false, () -> goTo(new PracticeRegistrationFrame(role)));
        navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.add(UITheme.subtitleLabel("Crear docente asesor"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.setOpaque(false);

        JTextField txtNombre = UITheme.textField();
        JTextField txtCorreo = UITheme.textField();
        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setFont(UITheme.BODY_FONT);
        JTextField txtArea = UITheme.textField();

        fields.add(UITheme.bodyLabel("Nombre completo"));
        fields.add(txtNombre);
        fields.add(UITheme.bodyLabel("Correo institucional"));
        fields.add(txtCorreo);
        fields.add(UITheme.bodyLabel("Contrasena"));
        fields.add(txtContrasena);
        fields.add(UITheme.bodyLabel("Area de asesoria"));
        fields.add(txtArea);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnSave = UITheme.primaryButton("Registrar docente");
        btnSave.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String contrasena = new String(txtContrasena.getPassword());
            String area = txtArea.getText().trim();

            if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Nombre, correo y contrasena son obligatorios.",
                        "Registro docente",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                registrationDao.registerTeacher(nombre, correo, contrasena, area);
                JOptionPane.showMessageDialog(this,
                        "Docente registrado correctamente.",
                        "Registro docente",
                        JOptionPane.INFORMATION_MESSAGE);
                txtNombre.setText("");
                txtCorreo.setText("");
                txtContrasena.setText("");
                txtArea.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar el docente.\nDetalle: " + ex.getMessage(),
                        "Error SQL",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        var btnClear = UITheme.secondaryButton("Limpiar");
        btnClear.addActionListener(e -> {
            txtNombre.setText("");
            txtCorreo.setText("");
            txtContrasena.setText("");
            txtArea.setText("");
        });

        actions.add(btnSave);
        actions.add(btnClear);

        card.add(fields, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        body.add(card, BorderLayout.CENTER);
    }
}
