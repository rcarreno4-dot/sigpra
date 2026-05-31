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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

public class BitacoraFrame extends BaseFrame {
    private final BitacoraDao bitacoraDao = new BitacoraDao();
    private JTable bitacoraTable;
    private DatePickerField dpFecha;
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
        configureBitacoraTable();
        attachSelectionSync();
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
        form.add(UITheme.bodyLabel("Fecha (seleccionable)"));
        dpFecha = new DatePickerField(LocalDate.now());
        form.add(dpFecha);
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

        JPanel actions = new JPanel(new GridLayout(1, 3, 6, 0));
        actions.setOpaque(false);
        var guardar = UITheme.primaryButton("Guardar");
        guardar.addActionListener(e -> saveStudentEntry());
        var actualizar = UITheme.secondaryButton("Actualizar");
        actualizar.addActionListener(e -> updateStudentEntry());
        var eliminar = UITheme.secondaryButton("Eliminar");
        eliminar.addActionListener(e -> deleteStudentEntry());
        actions.add(guardar);
        actions.add(actualizar);
        actions.add(eliminar);
        form.add(actions);
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

        String actividad = txtActividad.getText().trim();
        String horasTxt = txtHoras.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (actividad.isBlank() || horasTxt.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Actividad y horas son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fecha = dpFecha.getDate();
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
                model.insertRow(0, new Object[]{-(model.getRowCount() + 1L), fecha.toString(), actividad, horas.toPlainString(), "Pendiente", descripcion});
                clearStudentForm();
                JOptionPane.showMessageDialog(this,
                        "Entrada guardada en modo demo (sin persistencia en BD).",
                        "Bitacora Demo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            bitacoraDao.createStudentEntry(user.id(), fecha, actividad, descripcion, horas);
            refreshBitacoraTable();
            clearStudentForm();

            JOptionPane.showMessageDialog(this,
                    "Entrada guardada con estado PENDIENTE.",
                    "Bitacora",
                    JOptionPane.INFORMATION_MESSAGE);
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

    private void updateStudentEntry() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.ESTUDIANTE) {
            return;
        }
        int row = bitacoraTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una fila del historial para actualizar.",
                    "Bitacora",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String estado = String.valueOf(bitacoraTable.getModel().getValueAt(row, 4));
        if (!"Pendiente".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Solo las entradas en estado Pendiente se pueden actualizar.",
                    "Bitacora",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String actividad = txtActividad.getText().trim();
        String horasTxt = txtHoras.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        if (actividad.isBlank() || horasTxt.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Actividad y horas son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            long entryId = selectedEntryId();
            LocalDate fecha = dpFecha.getDate();
            BigDecimal horas = new BigDecimal(horasTxt.replace(',', '.'));
            boolean updated = AppSession.isDemoMode()
                    ? updateDemoRow(row, fecha, actividad, horas, descripcion)
                    : bitacoraDao.updateStudentEntry(user.id(), entryId, fecha, actividad, descripcion, horas);
            if (!updated) {
                JOptionPane.showMessageDialog(this,
                        "No fue posible actualizar. Verifica que la entrada siga en estado Pendiente.",
                        "Bitacora",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!AppSession.isDemoMode()) {
                refreshBitacoraTable();
            }
            clearStudentForm();
            JOptionPane.showMessageDialog(this,
                    "Entrada actualizada.",
                    "Bitacora",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Horas invalidas. Usa un numero como 3 o 3.5.",
                    "Formato de horas",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar en Oracle.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudentEntry() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.ESTUDIANTE) {
            return;
        }
        int row = bitacoraTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una fila del historial para eliminar.",
                    "Bitacora",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String estado = String.valueOf(bitacoraTable.getModel().getValueAt(row, 4));
        if (!"Pendiente".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Solo las entradas en estado Pendiente se pueden eliminar.",
                    "Bitacora",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Esta accion eliminara la entrada seleccionada. Deseas continuar?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean deleted = AppSession.isDemoMode()
                    ? deleteDemoRow(row)
                    : bitacoraDao.deleteStudentEntry(user.id(), selectedEntryId());
            if (!deleted) {
                JOptionPane.showMessageDialog(this,
                        "No fue posible eliminar. Verifica que la entrada siga en estado Pendiente.",
                        "Bitacora",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!AppSession.isDemoMode()) {
                refreshBitacoraTable();
            }
            clearStudentForm();
            JOptionPane.showMessageDialog(this,
                    "Entrada eliminada.",
                    "Bitacora",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar en Oracle.\nDetalle: " + ex.getMessage(),
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

    private void refreshBitacoraTable() {
        bitacoraTable.setModel(loadBitacoraModel());
        configureBitacoraTable();
    }

    private void configureBitacoraTable() {
        bitacoraTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bitacoraTable.getColumnModel().getColumn(0).setMinWidth(0);
        bitacoraTable.getColumnModel().getColumn(0).setMaxWidth(0);
        bitacoraTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        bitacoraTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        bitacoraTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        bitacoraTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        bitacoraTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        bitacoraTable.getColumnModel().getColumn(5).setPreferredWidth(260);
    }

    private void attachSelectionSync() {
        bitacoraTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || role != Role.ESTUDIANTE) {
                return;
            }
            int row = bitacoraTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            String fecha = String.valueOf(bitacoraTable.getModel().getValueAt(row, 1));
            dpFecha.setDate(LocalDate.parse(fecha));
            txtActividad.setText(String.valueOf(bitacoraTable.getModel().getValueAt(row, 2)));
            txtHoras.setText(String.valueOf(bitacoraTable.getModel().getValueAt(row, 3)));
            txtDescripcion.setText(String.valueOf(bitacoraTable.getModel().getValueAt(row, 5)));
        });
    }

    private long selectedEntryId() {
        int row = bitacoraTable.getSelectedRow();
        Object raw = bitacoraTable.getModel().getValueAt(row, 0);
        if (raw instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(String.valueOf(raw));
    }

    private void clearStudentForm() {
        if (dpFecha != null) {
            dpFecha.setDate(LocalDate.now());
        }
        if (txtActividad != null) {
            txtActividad.setText("");
        }
        if (txtHoras != null) {
            txtHoras.setText("");
        }
        if (txtDescripcion != null) {
            txtDescripcion.setText("");
        }
        if (bitacoraTable != null) {
            bitacoraTable.clearSelection();
        }
    }

    private boolean updateDemoRow(int row, LocalDate fecha, String actividad, BigDecimal horas, String descripcion) {
        DefaultTableModel model = (DefaultTableModel) bitacoraTable.getModel();
        model.setValueAt(fecha.toString(), row, 1);
        model.setValueAt(actividad, row, 2);
        model.setValueAt(horas.toPlainString(), row, 3);
        model.setValueAt(descripcion, row, 5);
        return true;
    }

    private boolean deleteDemoRow(int row) {
        DefaultTableModel model = (DefaultTableModel) bitacoraTable.getModel();
        model.removeRow(row);
        return true;
    }
}
