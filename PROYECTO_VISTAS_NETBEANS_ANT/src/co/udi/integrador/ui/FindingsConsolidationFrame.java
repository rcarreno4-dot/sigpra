package co.udi.integrador.ui;

import co.udi.integrador.data.DemoData;
import co.udi.integrador.data.FindingsDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class FindingsConsolidationFrame extends BaseFrame {
    private final FindingsDao findingsDao = new FindingsDao();
    private JTable table;
    private JTextArea txtAccion;

    public FindingsConsolidationFrame() {
        super("Consolidacion de Hallazgos", "Vacios, tensiones y fortalezas para mejora del programa", Role.DIRECTOR);
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "Solo la directora puede consolidar hallazgos institucionales.",
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
        navButton("Hallazgos", true, () -> { });
        navButton("Plantillas", false, () -> goTo(new TemplateConfigFrame()));
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        table = new JTable();
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Consolidado institucional"), BorderLayout.NORTH);
        tableCard.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        JPanel actionCard = UITheme.cardPanel();
        actionCard.setLayout(new BorderLayout(8, 8));
        actionCard.add(UITheme.subtitleLabel("Plan de mejora"), BorderLayout.NORTH);
        txtAccion = new JTextArea(4, 24);
        txtAccion.setLineWrap(true);
        txtAccion.setWrapStyleWord(true);
        txtAccion.setFont(UITheme.BODY_FONT);
        actionCard.add(new JScrollPane(txtAccion), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        var btnSave = UITheme.primaryButton("Registrar accion");
        btnSave.addActionListener(e -> registerAction());
        var btnReload = UITheme.secondaryButton("Recargar");
        btnReload.addActionListener(e -> reloadTable());
        actions.add(btnSave);
        actions.add(btnReload);
        actionCard.add(actions, BorderLayout.SOUTH);

        root.add(tableCard, BorderLayout.CENTER);
        root.add(actionCard, BorderLayout.SOUTH);
        body.add(root, BorderLayout.CENTER);
    }

    private void reloadTable() {
        try {
            table.setModel(findingsDao.listFindings());
        } catch (SQLException ex) {
            table.setModel(DemoData.findingsModel());
        }
    }

    private void registerAction() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un hallazgo para registrar la accion.",
                    "Hallazgos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = table.getValueAt(row, 0).toString();
        String accion = txtAccion.getText().trim();
        if (accion.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Describe la accion de mejora.",
                    "Hallazgos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Sesion no disponible.",
                    "Hallazgos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            findingsDao.registerImprovementAction(tipo, accion, user.id());
            txtAccion.setText("");
            JOptionPane.showMessageDialog(this,
                    "Accion registrada correctamente.",
                    "Hallazgos",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            addLocalAction(tipo, accion);
            txtAccion.setText("");
            JOptionPane.showMessageDialog(this,
                    "Accion guardada en modo local (demo) por error de BD.\nDetalle: " + ex.getMessage(),
                    "Hallazgos",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addLocalAction(String tipo, String accion) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (model.getColumnCount() == 0) {
            model = DemoData.findingsModel();
            table.setModel(model);
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (tipo.equals(String.valueOf(model.getValueAt(i, 0)))) {
                model.setValueAt(accion, i, 3);
                return;
            }
        }
        model.addRow(new Object[]{tipo, "1", "Registro local", accion});
    }
}
