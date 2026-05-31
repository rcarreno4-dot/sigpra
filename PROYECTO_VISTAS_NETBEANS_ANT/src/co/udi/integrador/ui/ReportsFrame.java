package co.udi.integrador.ui;

import co.udi.integrador.data.ReportsDao;
import co.udi.integrador.model.Role;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.awt.print.PrinterException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ReportsFrame extends BaseFrame {
    private final ReportsDao reportsDao = new ReportsDao();
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private JComboBox<String> cmbPeriodo;
    private JComboBox<String> cmbPrograma;
    private JComboBox<String> cmbEstado;
    private JTable table;

    public ReportsFrame() {
        super("Reportes", "Consolidado institucional de horas por periodo, programa y estado", Role.DIRECTOR);
        buildNav();
        buildBody();
        loadFilters();
        queryReport();
    }

    private void buildNav() {
        navButton("Dashboard", false, this::goDashboard);
        navButton("Plantillas", false, () -> goTo(new TemplateConfigFrame()));
        navButton("Aprobaciones", false, () -> goTo(new DirectorApprovalFrame()));
        navButton("Docentes", false, () -> goTo(new TeacherRegistrationFrame()));
        navButton("Asignaciones", false, () -> goTo(new PracticeRegistrationFrame(role)));
        navButton("Reportes", true, () -> { });
        navButton("Hallazgos", false, () -> goTo(new FindingsConsolidationFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        JPanel filters = UITheme.cardPanel();
        filters.setLayout(new GridLayout(2, 3, 8, 8));
        filters.add(UITheme.bodyLabel("Periodo"));
        filters.add(UITheme.bodyLabel("Programa"));
        filters.add(UITheme.bodyLabel("Estado"));

        cmbPeriodo = new JComboBox<>(new String[]{"Todos"});
        cmbPrograma = new JComboBox<>(new String[]{"Todos"});
        cmbEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "En curso", "Pend. aprobacion", "Finalizada"});
        filters.add(cmbPeriodo);
        filters.add(cmbPrograma);
        filters.add(cmbEstado);

        table = new JTable();
        JPanel tableCard = UITheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        tableCard.add(UITheme.subtitleLabel("Detalle por estudiante"), BorderLayout.NORTH);
        tableCard.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnQuery = UITheme.primaryButton("Consultar");
        btnQuery.addActionListener(e -> queryReport());
        var btnInforme = UITheme.secondaryButton("Generar informe (Vistas)");
        btnInforme.addActionListener(e -> showInformeVista());
        var btnPdf = UITheme.secondaryButton("Exportar PDF");
        btnPdf.addActionListener(e -> exportPdf());
        actions.add(btnQuery);
        actions.add(btnInforme);
        actions.add(btnPdf);

        root.add(filters, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        body.add(root, BorderLayout.CENTER);
    }

    private void loadFilters() {
        try {
            for (String p : reportsDao.listPeriods()) {
                cmbPeriodo.addItem(p);
            }
            for (String p : reportsDao.listPrograms()) {
                cmbPrograma.addItem(p);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar filtros de reportes.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void queryReport() {
        String periodo = (String) cmbPeriodo.getSelectedItem();
        String programa = (String) cmbPrograma.getSelectedItem();
        String estado = (String) cmbEstado.getSelectedItem();
        try {
            table.setModel(reportsDao.queryReport(periodo, programa, estado));
        } catch (SQLException ex) {
            table.setModel(new DefaultTableModel(
                    new String[]{"Estudiante", "Codigo", "Carrera", "Periodo", "Horas", "Meta horas", "Estado"}, 0));
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar el reporte.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInformeVista() {
        TableModel model = table.getModel();
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para mostrar el informe.", "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reporte = "Horas acumuladas vs requeridas";
        String periodo = String.valueOf(cmbPeriodo.getSelectedItem());
        String programa = String.valueOf(cmbPrograma.getSelectedItem());
        String estado = String.valueOf(cmbEstado.getSelectedItem());

        JDialog dialog = new JDialog(this, "Vista previa del informe", true);
        dialog.getContentPane().setBackground(new Color(241, 244, 248));
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);
        top.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 6, 20));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel("PLANTILLA DE CONTROL DE HORAS MENSUAL");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 44));
        lblTitle.setForeground(new Color(86, 136, 196));
        left.add(lblTitle);

        JLabel lblSub = new JLabel("SIGPRA - Modulo de Reportes (Vista Director)");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblSub.setForeground(new Color(20, 30, 45));
        left.add(lblSub);
        left.add(Box.createVerticalStrut(18));

        JLabel lblFecha = new JLabel("Fecha de emision: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        left.add(lblFecha);
        left.add(Box.createVerticalStrut(4));

        JLabel lblReporte = new JLabel("Reporte: " + reporte);
        lblReporte.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        left.add(lblReporte);
        left.add(Box.createVerticalStrut(4));

        JLabel lblPeriodo = new JLabel("Periodo: " + periodo);
        lblPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        left.add(lblPeriodo);
        left.add(Box.createVerticalStrut(4));

        JLabel lblPrograma = new JLabel("Programa: " + programa);
        lblPrograma.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        left.add(lblPrograma);
        left.add(Box.createVerticalStrut(4));

        JLabel lblEstado = new JLabel("Estado: " + estado);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        left.add(lblEstado);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(290, 180));
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.TOP);
        ImageIcon logoIcon = loadScaledLogo(220, 125);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("SIGPRA");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
            logoLabel.setForeground(new Color(31, 56, 117));
        }
        right.add(logoLabel, BorderLayout.NORTH);

        top.add(left, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);
        top.setPreferredSize(new Dimension(0, 250));

        JTable previewTable = new JTable(toReadOnlyModel(model));
        previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        previewTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        previewTable.setRowHeight(34);
        previewTable.setGridColor(new Color(138, 157, 185));
        previewTable.setShowGrid(true);

        previewTable.getTableHeader().setPreferredSize(new Dimension(100, 42));
        previewTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        previewTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setOpaque(true);
                c.setBackground(new Color(43, 66, 103));
                c.setForeground(Color.WHITE);
                c.setHorizontalAlignment(LEFT);
                return c;
            }
        });

        previewTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setOpaque(true);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(227, 233, 242) : new Color(243, 246, 251));
                    c.setForeground(new Color(20, 30, 45));
                }
                c.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        for (int i = 0; i < previewTable.getColumnCount(); i++) {
            previewTable.getColumnModel().getColumn(i).setPreferredWidth(180);
        }

        JScrollPane scroll = new JScrollPane(previewTable);
        scroll.getViewport().setBackground(new Color(241, 244, 248));
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 14, 0));
        var btnImprimir = UITheme.secondaryButton("Imprimir");
        btnImprimir.addActionListener(e -> printPreview(previewTable, reporte));
        var btnExportarPdf = UITheme.secondaryButton("Exportar PDF");
        btnExportarPdf.addActionListener(e -> exportPdfFromModel(model));
        var btnCerrar = UITheme.secondaryButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        actions.add(btnImprimir);
        actions.add(btnExportarPdf);
        actions.add(btnCerrar);

        dialog.add(top, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogW = Math.max(980, Math.min(1360, screen.width - 60));
        int dialogH = Math.max(620, Math.min(860, screen.height - 120));
        dialog.setSize(dialogW, dialogH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void exportPdf() {
        TableModel model = table.getModel();
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar en PDF.", "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        exportPdfFromModel(model);
    }

    private void printPreview(JTable previewTable, String reporte) {
        MessageFormat header = new MessageFormat("SIGPRA - " + reporte);
        MessageFormat footer = new MessageFormat("Pagina {0}");
        try {
            boolean done = previewTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (!done) {
                JOptionPane.showMessageDialog(this, "La impresion fue cancelada.", "Impresion", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo imprimir el informe.\nDetalle: " + ex.getMessage(),
                    "Error de impresion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportPdfFromModel(TableModel model) {
        JFileChooser chooser = new JFileChooser(defaultDir());
        chooser.setDialogTitle("Exportar informe a PDF");
        chooser.setSelectedFile(new java.io.File("INFORME_VISTAS_" + FILE_TS.format(LocalDateTime.now()) + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path out = chooser.getSelectedFile().toPath();
        try {
            SimplePdfWriter.writeStyledTableReportWithLogo(
                    out,
                    "PLANTILLA DE CONTROL DE HORAS MENSUAL",
                    "SIGPRA - Modulo de Reportes (Vista Director)",
                    buildMetaLines(),
                    extractHeaders(model),
                    extractRows(model),
                    loadLogoBytes());
            JOptionPane.showMessageDialog(this, "PDF generado en:\n" + out, "Exportacion correcta",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el PDF.\nDetalle: " + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon loadScaledLogo(int width, int height) {
        try (InputStream in = ReportsFrame.class.getResourceAsStream("/branding/sigpra-logo-main.jpeg")) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            ImageIcon raw = new ImageIcon(bytes);
            Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException ex) {
            return null;
        }
    }

    private DefaultTableModel toReadOnlyModel(TableModel source) {
        String[] headers = extractHeaders(source);
        Object[][] data = new Object[source.getRowCount()][source.getColumnCount()];
        for (int r = 0; r < source.getRowCount(); r++) {
            for (int c = 0; c < source.getColumnCount(); c++) {
                data[r][c] = source.getValueAt(r, c);
            }
        }
        return new DefaultTableModel(data, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private List<String> buildMetaLines() {
        String periodo = String.valueOf(cmbPeriodo.getSelectedItem());
        String programa = String.valueOf(cmbPrograma.getSelectedItem());
        String estado = String.valueOf(cmbEstado.getSelectedItem());
        List<String> meta = new ArrayList<>();
        meta.add("Fecha de emision: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        meta.add("Reporte: Horas acumuladas vs requeridas");
        meta.add("Periodo: " + periodo);
        meta.add("Programa: " + programa);
        meta.add("Estado: " + estado);
        return meta;
    }

    private String[] extractHeaders(TableModel model) {
        String[] headers = new String[model.getColumnCount()];
        for (int c = 0; c < model.getColumnCount(); c++) {
            headers[c] = model.getColumnName(c);
        }
        return headers;
    }

    private List<String[]> extractRows(TableModel model) {
        List<String[]> rows = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            String[] row = new String[model.getColumnCount()];
            for (int c = 0; c < model.getColumnCount(); c++) {
                Object value = model.getValueAt(r, c);
                row[c] = value == null ? "" : value.toString();
            }
            rows.add(row);
        }
        return rows;
    }

    private java.io.File defaultDir() {
        return new java.io.File(System.getProperty("user.home"), "Documents");
    }

    private byte[] loadLogoBytes() throws IOException {
        try (InputStream in = ReportsFrame.class.getResourceAsStream("/branding/sigpra-logo-main.jpeg")) {
            if (in == null) {
                throw new IOException("No se encontro /branding/sigpra-logo-main.jpeg");
            }
            return in.readAllBytes();
        }
    }
}
