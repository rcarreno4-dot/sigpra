package co.udi.integrador.ui;

import co.udi.integrador.data.BitacoraDao;
import co.udi.integrador.data.DemoData;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class BitacoraFrame extends BaseFrame {
    private final BitacoraDao bitacoraDao = new BitacoraDao();
    private JTable bitacoraTable;
    private JTextField txtFecha;
    private JTextField txtActividad;
    private JTextField txtHoras;
    private JTextArea txtDescripcion;

    public BitacoraFrame(Role role) {
        super("Bitacora de Practica", "Registro y seguimiento de actividades por actor", role);
        buildNav();
        buildBody();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Bitacora", true, () -> { });

        if (role == Role.ESTUDIANTE) {
            navButton("Evidencias", false, () -> goTo(new EvidenceFrame(role)));
        }
        if (role == Role.DOCENTE) {
            navButton("Validar", false, () -> goTo(new ValidationFrame()));
        }
        if (role == Role.DIRECTOR) {
            navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
            navButton("Docentes", false, () -> goTo(new TeacherRegistrationFrame()));
            navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        }
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);

        bitacoraTable = new JTable(loadBitacoraModel());
        JPanel historyCard = UITheme.cardPanel();
        historyCard.setLayout(new BorderLayout(8, 8));
        historyCard.add(UITheme.subtitleLabel("Historial de bitacora"), BorderLayout.NORTH);
        historyCard.add(UITheme.tableScroll(bitacoraTable), BorderLayout.CENTER);

        root.add(historyCard, BorderLayout.CENTER);

        if (role == Role.ESTUDIANTE) {
            root.add(studentFormCard(), BorderLayout.WEST);
        } else if (role == Role.DOCENTE) {
            root.add(teacherActionsCard(), BorderLayout.SOUTH);
        } else {
            root.add(directorSummaryCard(), BorderLayout.SOUTH);
        }

        body.add(root, BorderLayout.CENTER);
    }

    private JPanel studentFormCard() {
        JPanel form = UITheme.cardPanel();
        form.setLayout(new GridLayout(0, 1, 8, 8));
        form.add(UITheme.subtitleLabel("Nueva entrada"));
        form.add(UITheme.bodyLabel("Fecha"));
        txtFecha = UITheme.textField();
        txtFecha.setText(LocalDate.now().toString());
        form.add(txtFecha);
        form.add(UITheme.bodyLabel("Actividad"));
        txtActividad = UITheme.textField();
        form.add(txtActividad);
        form.add(UITheme.bodyLabel("Horas"));
        txtHoras = UITheme.textField();
        form.add(txtHoras);

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(UITheme.BODY_FONT);
        form.add(UITheme.bodyLabel("Descripcion"));
        form.add(new JScrollPane(txtDescripcion));

        var guardar = UITheme.primaryButton("Guardar en bitacora");
        guardar.addActionListener(e -> saveStudentEntry());
        form.add(guardar);
        return form;
    }

    private JPanel teacherActionsCard() {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BorderLayout(8, 8));
        panel.add(UITheme.subtitleLabel("Acciones de docente"), BorderLayout.NORTH);
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnValidate = UITheme.primaryButton("Ir a validacion");
        btnValidate.addActionListener(e -> goTo(new ValidationFrame()));
        actions.add(btnValidate);
        panel.add(actions, BorderLayout.CENTER);
        return panel;
    }

    private JPanel directorSummaryCard() {
        int[] sums = loadSummary();
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new GridLayout(1, 3, 8, 8));
        panel.add(kpi("Pendientes", String.valueOf(sums[0])));
        panel.add(kpi("Validadas", String.valueOf(sums[1])));
        panel.add(kpi("Rechazadas", String.valueOf(sums[2])));
        return panel;
    }

    private JPanel kpi(String title, String value) {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BorderLayout());
        panel.add(UITheme.subtitleLabel(title), BorderLayout.NORTH);
        panel.add(UITheme.titleLabel(value), BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel loadBitacoraModel() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            return DemoData.bitacoraModel();
        }

        try {
            return bitacoraDao.findByUser(user);
        } catch (SQLException ex) {
            return DemoData.bitacoraModel();
        }
    }

    private void saveStudentEntry() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.ESTUDIANTE) {
            JOptionPane.showMessageDialog(this,
                    "No hay una sesion de estudiante activa.",
                    "Sesion invalida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaTxt = txtFecha.getText().trim();
        String actividad = txtActividad.getText().trim();
        String horasTxt = txtHoras.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (fechaTxt.isBlank() || actividad.isBlank() || horasTxt.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Fecha, actividad y horas son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaTxt);
            BigDecimal horas = new BigDecimal(horasTxt.replace(',', '.'));
            if (horas.compareTo(BigDecimal.ZERO) <= 0 || horas.compareTo(new BigDecimal("12")) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Las horas deben estar entre 0.1 y 12.",
                        "Valor invalido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (AppSession.isDemoMode()) {
                DefaultTableModel model = (DefaultTableModel) bitacoraTable.getModel();
                model.insertRow(0, new Object[]{fecha.toString(), actividad, horas.toPlainString(), "Pendiente"});
                txtActividad.setText("");
                txtHoras.setText("");
                txtDescripcion.setText("");
                JOptionPane.showMessageDialog(this,
                        "Entrada guardada en modo demo (sin persistencia en BD).",
                        "Bitacora Demo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            bitacoraDao.createStudentEntry(user.id(), fecha, actividad, descripcion, horas);
            bitacoraTable.setModel(loadBitacoraModel());
            txtActividad.setText("");
            txtHoras.setText("");
            txtDescripcion.setText("");

            JOptionPane.showMessageDialog(this,
                    "Entrada guardada con estado PENDIENTE.",
                    "Bitacora",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha invalida. Usa formato YYYY-MM-DD.",
                    "Formato de fecha",
                    JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Horas invalidas. Usa un numero como 3 o 3.5.",
                    "Formato de horas",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar en Oracle.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int[] loadSummary() {
        try {
            return bitacoraDao.statusSummary();
        } catch (SQLException ex) {
            return new int[]{12, 78, 5};
        }
    }
}
