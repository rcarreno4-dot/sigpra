package co.udi.integrador.ui;

import co.udi.integrador.data.UserRegistrationDao;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Locale;
import co.udi.integrador.session.AppSession;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class StudentSelfRegistrationFrame extends JFrame {
    private final UserRegistrationDao registrationDao = new UserRegistrationDao();

    public StudentSelfRegistrationFrame() {
        super(AppIconFactory.windowTitle("Registro de Estudiante"));
        setIconImage(AppIconFactory.appIcon());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 560);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildForm() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(8, 8));

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setOpaque(false);
        header.add(new JLabel(AppIconFactory.wordmarkIcon(170, 52)), BorderLayout.WEST);
        header.add(UITheme.titleLabel("Autoregistro de estudiante"), BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.setOpaque(false);

        JTextField txtNombre = UITheme.textField();
        JTextField txtCorreo = UITheme.textField();
        JPasswordField txtContrasena = new JPasswordField();
        txtContrasena.setFont(UITheme.BODY_FONT);
        JTextField txtCodigo = UITheme.textField();
        JTextField txtPrograma = UITheme.textField();
        JTextField txtSemestre = UITheme.textField();

        fields.add(UITheme.bodyLabel("Nombre completo"));
        fields.add(txtNombre);
        fields.add(UITheme.bodyLabel("Correo institucional"));
        fields.add(txtCorreo);
        fields.add(UITheme.bodyLabel("Contrasena"));
        fields.add(txtContrasena);
        fields.add(UITheme.bodyLabel("Codigo estudiantil"));
        fields.add(txtCodigo);
        fields.add(UITheme.bodyLabel("Programa"));
        fields.add(txtPrograma);
        fields.add(UITheme.bodyLabel("Semestre"));
        fields.add(txtSemestre);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnRegister = UITheme.primaryButton("Crear cuenta de estudiante");
        btnRegister.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String contrasena = new String(txtContrasena.getPassword());
            String codigo = txtCodigo.getText().trim();
            String programa = txtPrograma.getText().trim();
            String semestreTxt = txtSemestre.getText().trim();

            if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || codigo.isBlank() || programa.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Completa todos los campos obligatorios.",
                        "Registro",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer semestre = null;
            if (!semestreTxt.isBlank()) {
                try {
                    semestre = Integer.parseInt(semestreTxt);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Semestre invalido. Usa un numero entero.",
                            "Registro",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (AppSession.isDemoMode()) {
                JOptionPane.showMessageDialog(this,
                        "Registro demo exitoso. En este modo no se guarda en base de datos.",
                        "Registro Demo",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
                return;
            }

            try {
                registrationDao.registerStudent(nombre, correo, contrasena, codigo, programa, semestre);
                JOptionPane.showMessageDialog(this,
                        "Registro exitoso. Ahora puedes iniciar sesion como estudiante.",
                        "Registro",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
            } catch (SQLException ex) {
                if (isPortableDbError(ex)) {
                    int option = JOptionPane.showConfirmDialog(this,
                            "No fue posible usar Oracle en esta ejecucion.\n"
                                    + "Deseas continuar en modo demo (sin guardar en BD)?",
                            "Oracle no disponible",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (option == JOptionPane.YES_OPTION) {
                        AppSession.setRunMode(co.udi.integrador.session.RunMode.DEMO);
                        JOptionPane.showMessageDialog(this,
                                "Registro demo exitoso. En este modo no se guarda en base de datos.",
                                "Registro Demo",
                                JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame().setVisible(true);
                        dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar el estudiante.\nDetalle: " + ex.getMessage(),
                        "Error SQL",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        var btnBack = UITheme.secondaryButton("Volver a inicio");
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        actions.add(btnRegister);
        actions.add(btnBack);

        card.add(fields, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
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
}
