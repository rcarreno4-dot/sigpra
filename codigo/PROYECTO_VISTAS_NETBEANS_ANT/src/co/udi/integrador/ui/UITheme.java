package co.udi.integrador.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

public final class UITheme {
    public static final Color BG = new Color(245, 248, 242);
    public static final Color CARD = new Color(255, 255, 255);
    public static final Color BRAND = new Color(0, 102, 204);
    public static final Color BRAND_SOFT = new Color(225, 238, 255);
    public static final Color ACCENT = new Color(0, 84, 168);
    public static final Color INK = new Color(35, 49, 58);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(227, 233, 236), 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
    );

    private UITheme() {
    }

    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(CARD_BORDER);
        return panel;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        applySolidButton(button);
        button.setBackground(BRAND);
        button.setForeground(Color.WHITE);
        button.setFont(BODY_FONT.deriveFont(Font.BOLD));
        button.setMargin(new Insets(8, 12, 8, 12));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        applySolidButton(button);
        button.setBackground(new Color(32, 72, 123));
        button.setForeground(Color.WHITE);
        button.setFont(BODY_FONT.deriveFont(Font.BOLD));
        button.setMargin(new Insets(8, 12, 8, 12));
        return button;
    }

    public static JButton navButton(String text, boolean active) {
        JButton button = new JButton(text);
        applySolidButton(button);
        button.setFont(BODY_FONT);
        button.setMargin(new Insets(6, 10, 6, 10));
        if (active) {
            button.setBackground(ACCENT);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(BRAND_SOFT);
            button.setForeground(new Color(15, 52, 96));
        }
        return button;
    }

    private static void applySolidButton(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(INK);
        return label;
    }

    public static JLabel subtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(new Color(81, 98, 109));
        return label;
    }

    public static JLabel bodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        return label;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setFont(BODY_FONT);
        return field;
    }

    public static JScrollPane tableScroll(JTable table) {
        table.setRowHeight(24);
        table.setFont(BODY_FONT);
        table.getTableHeader().setFont(BODY_FONT.deriveFont(Font.BOLD));
        return new JScrollPane(table);
    }
}
