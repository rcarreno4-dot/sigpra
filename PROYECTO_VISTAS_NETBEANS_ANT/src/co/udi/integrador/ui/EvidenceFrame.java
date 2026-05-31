package co.udi.integrador.ui;

import co.udi.integrador.data.EvidenceDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.ComboItem;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EvidenceFrame extends BaseFrame {
    private final EvidenceDao evidenceDao = new EvidenceDao();
    private JComboBox<ComboItem> cmbBitacora;
    private JComboBox<String> cmbTipo;
    private JTextField txtRuta;
    private JTextArea txtComentario;

    public EvidenceFrame(Role role) {
        super("Carga de Evidencias", "Anexos por actividad para soporte academico", role);
        buildNav();
        buildBody();
        loadData();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Bitacora", false, () -> goTo(new BitacoraFrame(role)));
        navButton("Evidencias", true, () -> { });
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridLayout(0, 2, 10, 10));

        card.add(UITheme.bodyLabel("Actividad asociada"));
        cmbBitacora = new JComboBox<>();
        card.add(cmbBitacora);

        card.add(UITheme.bodyLabel("Tipo de archivo"));
        cmbTipo = new JComboBox<>(new String[]{"PDF", "IMG", "DOC", "OTRO"});
        card.add(cmbTipo);

        card.add(UITheme.bodyLabel("Ruta de archivo"));
        txtRuta = UITheme.textField();
        card.add(txtRuta);

        txtComentario = new JTextArea(4, 20);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setFont(UITheme.BODY_FONT);
        card.add(UITheme.bodyLabel("Comentario"));
        card.add(new JScrollPane(txtComentario));

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnSave = UITheme.primaryButton("Cargar evidencia");
        btnSave.addActionListener(e -> saveEvidence());
        actions.add(btnSave);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);
        root.add(card, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        body.add(root, BorderLayout.CENTER);
    }

    private void loadData() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            return;
        }
        if (AppSession.isDemoMode()) {
            loadDemoBitacoraOptions();
            return;
        }
        try {
            List<ComboItem> entries = evidenceDao.listBitacoraForStudent(user.id());
            cmbBitacora.removeAllItems();
            for (ComboItem item : entries) {
                cmbBitacora.addItem(item);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar actividades de bitacora.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDemoBitacoraOptions() {
        cmbBitacora.removeAllItems();
        cmbBitacora.addItem(new ComboItem(801L, "2026-03-16 - Diseno de taller de lectura"));
        cmbBitacora.addItem(new ComboItem(802L, "2026-03-17 - Aplicacion de estrategia"));
        cmbBitacora.addItem(new ComboItem(803L, "2026-03-18 - Seguimiento individual"));
    }

    private void saveEvidence() {
        ComboItem bitacora = (ComboItem) cmbBitacora.getSelectedItem();
        if (bitacora == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay actividad de bitacora disponible.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = (String) cmbTipo.getSelectedItem();
        String ruta = txtRuta.getText().trim();
        String comentario = txtComentario.getText().trim();
        if (ruta.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "La ruta del archivo es obligatoria.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fileName = ruta.replace("\\", "/");
        int slash = fileName.lastIndexOf('/');
        if (slash >= 0 && slash < fileName.length() - 1) {
            fileName = fileName.substring(slash + 1);
        }

        if (AppSession.isDemoMode()) {
            txtRuta.setText("");
            txtComentario.setText("");
            JOptionPane.showMessageDialog(this,
                    "Evidencia registrada en modo demo (sin persistencia en BD).",
                    "Evidencias Demo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            evidenceDao.insertEvidence(bitacora.id(), tipo, fileName, ruta, comentario);
            txtRuta.setText("");
            txtComentario.setText("");
            JOptionPane.showMessageDialog(this,
                    "Evidencia registrada correctamente.",
                    "Evidencias",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la evidencia.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
