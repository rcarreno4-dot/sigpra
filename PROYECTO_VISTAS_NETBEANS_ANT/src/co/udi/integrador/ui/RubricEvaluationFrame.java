package co.udi.integrador.ui;

import co.udi.integrador.data.DemoData;
import co.udi.integrador.data.RubricDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.ComboItem;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class RubricEvaluationFrame extends BaseFrame {
    private final RubricDao rubricDao = new RubricDao();
    private JComboBox<ComboItem> cmbPractice;
    private JSpinner spnPlaneacion;
    private JSpinner spnEjecucion;
    private JSpinner spnReflexion;
    private JSpinner spnEvidencias;
    private JLabel lblPromedio;
    private JTextArea txtObservacion;
    private JTable table;

    public RubricEvaluationFrame() {
        super("Evaluacion por Rubrica", "Valoracion docente de practica academica por criterios", Role.DOCENTE);
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DOCENTE) {
            JOptionPane.showMessageDialog(this,
                    "Solo el docente asesor puede registrar evaluaciones de rubrica.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            closeAllAndReturnToLogin();
            return;
        }
        buildNav();
        buildBody();
        loadPractices();
        reloadTable();
        updateAverage();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Validar", false, () -> goTo(new ValidationFrame()));
        navButton("Rubrica", true, () -> { });
        navButton("Bitacora", false, () -> goTo(new BitacoraFrame(role)));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        JPanel formCard = UITheme.cardPanel();
        formCard.setLayout(new BorderLayout(8, 8));
        formCard.add(UITheme.subtitleLabel("Registro de evaluacion"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setOpaque(false);
        form.add(UITheme.bodyLabel("Practica"));
        cmbPractice = new JComboBox<>();
        form.add(cmbPractice);
        form.add(UITheme.bodyLabel("Planeacion didactica (1-5)"));
        spnPlaneacion = spinner();
        form.add(spnPlaneacion);
        form.add(UITheme.bodyLabel("Ejecucion pedagogica (1-5)"));
        spnEjecucion = spinner();
        form.add(spnEjecucion);
        form.add(UITheme.bodyLabel("Reflexion profesional (1-5)"));
        spnReflexion = spinner();
        form.add(spnReflexion);
        form.add(UITheme.bodyLabel("Calidad de evidencias (1-5)"));
        spnEvidencias = spinner();
        form.add(spnEvidencias);
        form.add(UITheme.bodyLabel("Promedio"));
        lblPromedio = UITheme.titleLabel("0.00");
        form.add(lblPromedio);
        form.add(UITheme.bodyLabel("Observacion"));
        txtObservacion = new JTextArea(3, 24);
        txtObservacion.setLineWrap(true);
        txtObservacion.setWrapStyleWord(true);
        txtObservacion.setFont(UITheme.BODY_FONT);
        form.add(new JScrollPane(txtObservacion));

        ChangeListener listener = e -> updateAverage();
        spnPlaneacion.addChangeListener(listener);
        spnEjecucion.addChangeListener(listener);
        spnReflexion.addChangeListener(listener);
        spnEvidencias.addChangeListener(listener);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        var btnSave = UITheme.primaryButton("Guardar evaluacion");
        btnSave.addActionListener(e -> saveEvaluation());
        var btnClear = UITheme.secondaryButton("Limpiar");
        btnClear.addActionListener(e -> clearForm());
        actions.add(btnSave);
        actions.add(btnClear);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(form, BorderLayout.CENTER);
        top.add(actions, BorderLayout.SOUTH);
        formCard.add(top, BorderLayout.CENTER);

        table = new JTable();
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Evaluaciones recientes"), BorderLayout.NORTH);
        tableCard.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        root.add(formCard, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
        body.add(root, BorderLayout.CENTER);
    }

    private JSpinner spinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(4, 1, 5, 1));
        spinner.setFont(UITheme.BODY_FONT);
        return spinner;
    }

    private void loadPractices() {
        cmbPractice.removeAllItems();
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            for (ComboItem item : DemoData.rubricPractices()) {
                cmbPractice.addItem(item);
            }
            return;
        }
        try {
            for (ComboItem item : rubricDao.listPracticesForTeacher(user.id())) {
                cmbPractice.addItem(item);
            }
            if (cmbPractice.getItemCount() == 0) {
                for (ComboItem item : DemoData.rubricPractices()) {
                    cmbPractice.addItem(item);
                }
            }
        } catch (SQLException ex) {
            for (ComboItem item : DemoData.rubricPractices()) {
                cmbPractice.addItem(item);
            }
        }
    }

    private void saveEvaluation() {
        ComboItem selected = (ComboItem) cmbPractice.getSelectedItem();
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (selected == null || user == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay practica seleccionada o sesion activa.",
                    "Rubrica",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planeacion = (Integer) spnPlaneacion.getValue();
        int ejecucion = (Integer) spnEjecucion.getValue();
        int reflexion = (Integer) spnReflexion.getValue();
        int evidencias = (Integer) spnEvidencias.getValue();
        String observacion = txtObservacion.getText().trim();

        try {
            rubricDao.saveEvaluation(selected.id(), user.id(), planeacion, ejecucion, reflexion, evidencias, observacion);
            reloadTable();
            clearForm();
            JOptionPane.showMessageDialog(this,
                    "Evaluacion registrada correctamente.",
                    "Rubrica",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            addLocalEvaluation(selected);
            clearForm();
            JOptionPane.showMessageDialog(this,
                    "Se registro en modo local (demo) por error de BD.\nDetalle: " + ex.getMessage(),
                    "Rubrica",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void reloadTable() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            table.setModel(DemoData.rubricEvaluationModel());
            return;
        }
        try {
            table.setModel(rubricDao.listRecentEvaluations(user.id()));
        } catch (SQLException ex) {
            table.setModel(DemoData.rubricEvaluationModel());
        }
    }

    private void addLocalEvaluation(ComboItem selected) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (model.getColumnCount() == 0) {
            model = DemoData.rubricEvaluationModel();
            table.setModel(model);
        }
        int nextId = 9000 + model.getRowCount() + 1;
        String student = selected.label();
        int dash = student.indexOf(" - ");
        if (dash > 0) {
            student = student.substring(dash + 3);
        }
        model.addRow(new Object[]{
                String.valueOf(nextId),
                String.valueOf(selected.id()),
                student,
                java.time.LocalDate.now().toString(),
                lblPromedio.getText(),
                "REGISTRADA"
        });
    }

    private void updateAverage() {
        int p1 = (Integer) spnPlaneacion.getValue();
        int p2 = (Integer) spnEjecucion.getValue();
        int p3 = (Integer) spnReflexion.getValue();
        int p4 = (Integer) spnEvidencias.getValue();
        double avg = (p1 + p2 + p3 + p4) / 4.0d;
        lblPromedio.setText(String.format(java.util.Locale.US, "%.2f", avg));
    }

    private void clearForm() {
        spnPlaneacion.setValue(4);
        spnEjecucion.setValue(4);
        spnReflexion.setValue(4);
        spnEvidencias.setValue(4);
        txtObservacion.setText("");
        updateAverage();
    }
}
