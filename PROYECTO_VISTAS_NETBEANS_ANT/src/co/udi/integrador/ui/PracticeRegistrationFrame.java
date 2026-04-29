package co.udi.integrador.ui;

import co.udi.integrador.data.PracticeDao;
import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.ComboItem;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PracticeRegistrationFrame extends BaseFrame {
    private final PracticeDao practiceDao = new PracticeDao();
    private JComboBox<ComboItem> cmbStudent;
    private JTextField txtEstudiante;
    private JTextField txtCodigo;
    private JTextField txtPrograma;
    private JTextField txtPeriodo;
    private JComboBox<ComboItem> cmbEntidad;
    private JComboBox<ComboItem> cmbDocente;
    private JComboBox<ComboItem> cmbDirector;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextArea txtObjetivo;
    private long selectedStudentId = -1;

    public PracticeRegistrationFrame(Role role) {
        super("Registro de Practica", "Formulario principal para asignacion y registro", role);
        if (role != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "La asignacion y registro de practica solo la realiza la directora.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            goDashboard();
            dispose();
            return;
        }
        buildNav();
        buildBody();
        loadInitialData();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Registro", true, () -> { });
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Docentes", false, () -> goTo(new TeacherRegistrationFrame()));
        navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridLayout(0, 2, 10, 10));

        if (role == Role.DIRECTOR) {
            card.add(UITheme.bodyLabel("Estudiante"));
            cmbStudent = new JComboBox<>();
            cmbStudent.addActionListener(e -> fillStudentFromCombo());
            card.add(cmbStudent);
        }

        card.add(UITheme.bodyLabel("Nombre estudiante"));
        txtEstudiante = UITheme.textField();
        card.add(txtEstudiante);

        card.add(UITheme.bodyLabel("Codigo"));
        txtCodigo = UITheme.textField();
        card.add(txtCodigo);

        card.add(UITheme.bodyLabel("Programa"));
        txtPrograma = UITheme.textField();
        card.add(txtPrograma);

        card.add(UITheme.bodyLabel("Periodo academico"));
        txtPeriodo = UITheme.textField();
        txtPeriodo.setText("2026-1");
        card.add(txtPeriodo);

        card.add(UITheme.bodyLabel("Entidad receptora"));
        cmbEntidad = new JComboBox<>();
        card.add(cmbEntidad);

        card.add(UITheme.bodyLabel("Docente asesor"));
        cmbDocente = new JComboBox<>();
        card.add(cmbDocente);

        card.add(UITheme.bodyLabel("Director de programa"));
        cmbDirector = new JComboBox<>();
        card.add(cmbDirector);

        card.add(UITheme.bodyLabel("Fecha inicio (YYYY-MM-DD)"));
        txtFechaInicio = UITheme.textField();
        txtFechaInicio.setText(LocalDate.now().toString());
        card.add(txtFechaInicio);

        card.add(UITheme.bodyLabel("Fecha fin (YYYY-MM-DD)"));
        txtFechaFin = UITheme.textField();
        txtFechaFin.setText(LocalDate.now().plusMonths(4).toString());
        card.add(txtFechaFin);

        txtObjetivo = new JTextArea(4, 20);
        txtObjetivo.setLineWrap(true);
        txtObjetivo.setWrapStyleWord(true);
        txtObjetivo.setFont(UITheme.BODY_FONT);
        card.add(UITheme.bodyLabel("Objetivo de practica"));
        card.add(new JScrollPane(txtObjetivo));

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var saveBtn = UITheme.primaryButton("Guardar registro");
        saveBtn.addActionListener(e -> savePractice());
        var clearBtn = UITheme.secondaryButton("Limpiar");
        clearBtn.addActionListener(e -> clearForm());
        actions.add(saveBtn);
        actions.add(clearBtn);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);
        root.add(card, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        body.add(root, BorderLayout.CENTER);
    }

    private void loadInitialData() {
        if (AppSession.isDemoMode()) {
            loadDemoCombos();
            configureRoleDefaultsDemo();
            return;
        }

        try {
            loadCombos();
            configureRoleDefaults();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar informacion de registro.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDemoCombos() {
        fillCombo(cmbEntidad, List.of(
                new ComboItem(101L, "I.E. Tecnico Damaso Zapata (Bucaramanga)"),
                new ComboItem(102L, "Colegio Tecnico Vicente Azuero (Floridablanca)"),
                new ComboItem(103L, "I.E. Luis Carlos Galan Sarmiento (Giron)"),
                new ComboItem(104L, "I.E. La Argentina (Piedecuesta)")
        ));

        fillCombo(cmbDocente, List.of(
                new ComboItem(201L, "Luis Perez"),
                new ComboItem(202L, "Daniel Rojas")
        ));

        fillCombo(cmbDirector, List.of(
                new ComboItem(301L, "Ana Ruiz (Directora)")
        ));

        if (cmbStudent != null) {
            fillCombo(cmbStudent, List.of(
                    new ComboItem(401L, "Maria Gomez - 20261001"),
                    new ComboItem(402L, "Johan Diaz - 20261002")
            ));
        }
    }

    private void loadCombos() throws SQLException {
        fillCombo(cmbEntidad, practiceDao.listEntities());
        fillCombo(cmbDocente, practiceDao.listTeachers());
        fillCombo(cmbDirector, practiceDao.listDirectors());
        if (cmbStudent != null) {
            fillCombo(cmbStudent, practiceDao.listStudents());
        }
    }

    private void configureRoleDefaults() throws SQLException {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            return;
        }

        fillStudentFromCombo();
    }

    private void configureRoleDefaultsDemo() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null) {
            return;
        }

        fillStudentFromCombo();
    }

    private void fillStudentFromCombo() {
        ComboItem item = (ComboItem) cmbStudent.getSelectedItem();
        if (item == null) {
            return;
        }
        if (AppSession.isDemoMode()) {
            selectedStudentId = item.id();
            String label = item.label();
            String[] parts = label.split("-");
            txtEstudiante.setText(parts.length > 0 ? parts[0].trim() : label);
            txtCodigo.setText(parts.length > 1 ? parts[1].trim() : "DEMO-" + item.id());
            txtPrograma.setText("Licenciatura (Demo)");
            return;
        }
        try {
            selectedStudentId = item.id();
            fillStudentFields(practiceDao.findStudentProfileByStudentId(item.id()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar datos del estudiante.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillStudentFields(PracticeDao.StudentProfile profile) {
        if (profile == null) {
            return;
        }
        txtEstudiante.setText(profile.nombre());
        txtCodigo.setText(profile.codigo());
        txtPrograma.setText(profile.programa());
    }

    private void savePractice() {
        try {
            if (role != Role.DIRECTOR) {
                throw new IllegalArgumentException("Solo la directora puede registrar asignaciones de practica.");
            }
            if (selectedStudentId <= 0) {
                throw new IllegalArgumentException("Selecciona un estudiante valido.");
            }

            ComboItem entity = (ComboItem) cmbEntidad.getSelectedItem();
            ComboItem teacher = (ComboItem) cmbDocente.getSelectedItem();
            ComboItem director = (ComboItem) cmbDirector.getSelectedItem();
            if (entity == null || teacher == null || director == null) {
                throw new IllegalArgumentException("Entidad, docente y director son obligatorios.");
            }

            String periodo = txtPeriodo.getText().trim();
            String objetivo = txtObjetivo.getText().trim();
            if (periodo.isBlank()) {
                throw new IllegalArgumentException("El periodo academico es obligatorio.");
            }

            LocalDate inicio = LocalDate.parse(txtFechaInicio.getText().trim());
            LocalDate fin = LocalDate.parse(txtFechaFin.getText().trim());
            if (fin.isBefore(inicio)) {
                throw new IllegalArgumentException("La fecha fin no puede ser menor que fecha inicio.");
            }

            if (AppSession.isDemoMode()) {
                JOptionPane.showMessageDialog(this,
                        "Registro de practica guardado en modo demo (sin persistencia en BD).",
                        "Registro Demo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            practiceDao.createPractice(
                    selectedStudentId,
                    teacher.id(),
                    director.id(),
                    entity.id(),
                    periodo,
                    inicio,
                    fin,
                    objetivo
            );

            JOptionPane.showMessageDialog(this,
                    "Registro de practica guardado correctamente.",
                    "Registro",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha invalido. Usa YYYY-MM-DD.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la practica.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtPeriodo.setText("2026-1");
        txtFechaInicio.setText(LocalDate.now().toString());
        txtFechaFin.setText(LocalDate.now().plusMonths(4).toString());
        txtObjetivo.setText("");
        txtEstudiante.setText("");
        txtCodigo.setText("");
        txtPrograma.setText("");
        selectedStudentId = -1;
        if (cmbStudent.getItemCount() > 0) {
            cmbStudent.setSelectedIndex(0);
            fillStudentFromCombo();
        }
    }

    private void fillCombo(JComboBox<ComboItem> combo, List<ComboItem> items) {
        combo.removeAllItems();
        for (ComboItem item : items) {
            combo.addItem(item);
        }
    }
}
