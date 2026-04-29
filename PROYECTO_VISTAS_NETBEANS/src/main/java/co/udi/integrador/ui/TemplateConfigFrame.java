package co.udi.integrador.ui;

import co.udi.integrador.data.DemoData;
import co.udi.integrador.data.TemplateDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class TemplateConfigFrame extends BaseFrame {
    private final TemplateDao templateDao = new TemplateDao();
    private JTextField txtPeriodo;
    private JTextField txtModalidad;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTable table;

    public TemplateConfigFrame() {
        super("Plantillas de Bitacora", "Configuracion de plantilla por periodo y modalidad", Role.DIRECTOR);
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "Solo la directora puede configurar plantillas de bitacora.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            closeAllAndReturnToLogin();
            return;
        }
        buildNav();
        buildBody();
        reloadTable();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Plantillas", true, () -> { });
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        navButton("Hallazgos", false, () -> goTo(new FindingsConsolidationFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        JPanel formCard = UITheme.cardPanel();
        formCard.setLayout(new BorderLayout(8, 8));
        formCard.add(UITheme.subtitleLabel("Nueva plantilla"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setOpaque(false);
        form.add(UITheme.bodyLabel("Periodo (ej. 2026-1)"));
        txtPeriodo = UITheme.textField();
        form.add(txtPeriodo);
        form.add(UITheme.bodyLabel("Modalidad"));
        txtModalidad = UITheme.textField();
        form.add(txtModalidad);
        form.add(UITheme.bodyLabel("Nombre plantilla"));
        txtNombre = UITheme.textField();
        form.add(txtNombre);
        form.add(UITheme.bodyLabel("Descripcion"));
        txtDescripcion = new JTextArea(3, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(UITheme.BODY_FONT);
        form.add(new JScrollPane(txtDescripcion));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        var btnSave = UITheme.primaryButton("Guardar plantilla");
        btnSave.addActionListener(e -> saveTemplate());
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
        tableCard.add(UITheme.subtitleLabel("Plantillas configuradas"), BorderLayout.NORTH);
        tableCard.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        root.add(formCard, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
        body.add(root, BorderLayout.CENTER);
    }

    private void saveTemplate() {
        String periodo = txtPeriodo.getText().trim();
        String modalidad = txtModalidad.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (periodo.isBlank() || modalidad.isBlank() || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Periodo, modalidad y nombre son obligatorios.",
                    "Plantillas",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Sesion no disponible.",
                    "Plantillas",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            templateDao.createTemplate(periodo, modalidad, nombre, descripcion, user.id());
            reloadTable();
            clearForm();
            JOptionPane.showMessageDialog(this,
                    "Plantilla registrada correctamente.",
                    "Plantillas",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            addLocalTemplate(periodo, modalidad, nombre);
            JOptionPane.showMessageDialog(this,
                    "Se registro en modo local (demo) por error de BD.\nDetalle: " + ex.getMessage(),
                    "Plantillas",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void reloadTable() {
        try {
            table.setModel(templateDao.listTemplates());
        } catch (SQLException ex) {
            table.setModel(DemoData.templateConfigModel());
        }
    }

    private void addLocalTemplate(String periodo, String modalidad, String nombre) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (model.getColumnCount() == 0) {
            model = DemoData.templateConfigModel();
            table.setModel(model);
        }
        int next = model.getRowCount() + 1;
        model.addRow(new Object[]{
                String.valueOf(next),
                periodo,
                modalidad,
                nombre,
                "ACTIVA",
                "1"
        });
    }

    private void clearForm() {
        txtPeriodo.setText("");
        txtModalidad.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
