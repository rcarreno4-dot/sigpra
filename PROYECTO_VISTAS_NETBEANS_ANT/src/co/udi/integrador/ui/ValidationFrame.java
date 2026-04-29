package co.udi.integrador.ui;

import co.udi.integrador.data.ValidationDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ValidationFrame extends BaseFrame {
    private final ValidationDao validationDao = new ValidationDao();
    private JTable table;

    public ValidationFrame() {
        super("Validacion de Actividades", "Revision docente de entradas de bitacora", Role.DOCENTE);
        buildNav();
        buildBody();
        reloadTable();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Bitacora", false, () -> goTo(new BitacoraFrame(role)));
        navButton("Validar", true, () -> { });
        navButton("Rubrica", false, () -> goTo(new RubricEvaluationFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        table = new JTable();
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.add(UITheme.subtitleLabel("Entradas de bitacora del docente"), BorderLayout.NORTH);
        card.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnApprove = UITheme.primaryButton("Validar seleccion");
        btnApprove.addActionListener(e -> processValidation(true));
        var btnReject = UITheme.secondaryButton("Rechazar seleccion");
        btnReject.addActionListener(e -> processValidation(false));
        actions.add(btnApprove);
        actions.add(btnReject);

        root.add(card, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        body.add(root, BorderLayout.CENTER);
    }

    private void processValidation(boolean approved) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una fila para validar o rechazar.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Sesion no disponible.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long bitacoraId = Long.parseLong(table.getValueAt(row, 0).toString());
        String obs = JOptionPane.showInputDialog(this,
                approved ? "Observacion (opcional):" : "Motivo de rechazo:");
        if (!approved && (obs == null || obs.isBlank())) {
            JOptionPane.showMessageDialog(this,
                    "Debes indicar motivo para rechazo.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (obs == null) {
            obs = "";
        }

        try {
            validationDao.validate(bitacoraId, user.id(), approved, obs.trim());
            reloadTable();
            JOptionPane.showMessageDialog(this,
                    approved ? "Entrada validada." : "Entrada rechazada.",
                    "Validacion",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar la entrada.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadTable() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            table.setModel(emptyModel());
            return;
        }
        try {
            DefaultTableModel model = validationDao.listForTeacher(user.id());
            table.setModel(model);
        } catch (SQLException ex) {
            table.setModel(emptyModel());
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar validaciones.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultTableModel emptyModel() {
        return new DefaultTableModel(new String[]{"ID", "Estudiante", "Fecha", "Actividad", "Horas", "Estado"}, 0);
    }
}
