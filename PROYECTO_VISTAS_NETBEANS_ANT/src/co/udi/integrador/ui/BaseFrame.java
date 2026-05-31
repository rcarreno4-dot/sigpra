package co.udi.integrador.ui;

import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class BaseFrame extends JFrame {
    protected final Role role;
    protected final JPanel body = new JPanel(new BorderLayout(12, 12));
    protected final JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

    protected BaseFrame(String title, String subtitle, Role role) {
        super(AppIconFactory.windowTitle(title));
        this.role = role;
        setIconImage(AppIconFactory.appIcon());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 700);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UITheme.BG);
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 18, 16, 18));

        root.add(buildHeader(title, subtitle), BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(UITheme.titleLabel(title));
        left.add(UITheme.subtitleLabel(subtitle));

        String modeTag = AppSession.isDemoMode() ? " | Modo Demo" : " | Modo Oracle";
        JLabel roleTag = UITheme.bodyLabel("Rol activo: " + role.label() + modeTag);
        roleTag.setOpaque(true);
        roleTag.setBackground(UITheme.BRAND);
        roleTag.setForeground(java.awt.Color.WHITE);
        roleTag.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel(AppIconFactory.wordmarkIcon(180, 56));
        logo.setAlignmentX(1.0f);
        roleTag.setAlignmentX(1.0f);
        right.add(logo);
        right.add(new JLabel(" "));
        right.add(roleTag);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

        nav.setOpaque(true);
        nav.setBackground(UITheme.BRAND_SOFT);
        nav.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));

        header.add(row);
        header.add(new JLabel(" "));
        header.add(nav);
        return header;
    }

    protected JButton navButton(String label, boolean active, Runnable action) {
        JButton button = UITheme.navButton(label, active);
        button.addActionListener(e -> action.run());
        nav.add(button);
        return button;
    }

    protected void goTo(JFrame next) {
        next.setVisible(true);
        dispose();
    }

    protected void goDashboard() {
        switch (role) {
            case ESTUDIANTE -> goTo(new StudentDashboardFrame());
            case DOCENTE -> goTo(new TeacherDashboardFrame());
            case DIRECTOR -> goTo(new DirectorDashboardFrame());
        }
    }

    protected void closeAllAndReturnToLogin() {
        AppSession.clear();
        for (Frame frame : Frame.getFrames()) {
            if (frame.isDisplayable()) {
                frame.dispose();
            }
        }
        new LoginFrame().setVisible(true);
    }
}
