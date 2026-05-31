package co.udi.integrador.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DatePickerField extends JPanel {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate value;
    private final JTextField txtDate;

    public DatePickerField(LocalDate initialValue) {
        super(new BorderLayout(6, 0));
        this.value = initialValue == null ? LocalDate.now() : initialValue;

        txtDate = UITheme.textField();
        txtDate.setEditable(false);
        txtDate.setText(FMT.format(this.value));
        txtDate.setToolTipText("Haz clic para abrir calendario");
        txtDate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openPicker();
            }
        });

        JButton btnCalendar = UITheme.secondaryButton("...");
        btnCalendar.setToolTipText("Abrir calendario");
        btnCalendar.setPreferredSize(new Dimension(40, 32));
        btnCalendar.addActionListener(e -> openPicker());

        add(txtDate, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);
    }

    public LocalDate getDate() {
        return value;
    }

    public void setDate(LocalDate newValue) {
        this.value = newValue == null ? LocalDate.now() : newValue;
        txtDate.setText(FMT.format(this.value));
    }

    private void openPicker() {
        LocalDate selected = DatePickerDialog.pickDate(this, value);
        if (selected != null) {
            setDate(selected);
        }
    }

    private static final class DatePickerDialog extends JDialog {
        private static final String[] DOW = {"L", "M", "M", "J", "V", "S", "D"};
        private final JLabel lblMonth = new JLabel("", SwingConstants.CENTER);
        private final JPanel gridDays = new JPanel(new GridLayout(6, 7, 4, 4));
        private final JComboBox<Integer> cmbYear = new JComboBox<>();
        private final JComboBox<MonthItem> cmbMonth = new JComboBox<>();
        private YearMonth showingMonth;
        private LocalDate selectedDate;
        private LocalDate pickedDate;

        private DatePickerDialog(Window owner, LocalDate initialDate) {
            super(owner, "Seleccionar fecha", ModalityType.APPLICATION_MODAL);
            this.selectedDate = initialDate == null ? LocalDate.now() : initialDate;
            this.showingMonth = YearMonth.from(this.selectedDate);
            setLayout(new BorderLayout(8, 8));
            getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel top = new JPanel(new BorderLayout(8, 0));
            JButton prev = UITheme.secondaryButton("<");
            JButton next = UITheme.secondaryButton(">");
            JPanel selectors = new JPanel(new GridLayout(1, 2, 8, 0));
            selectors.add(cmbMonth);
            selectors.add(cmbYear);

            int currentYear = LocalDate.now().getYear();
            for (int y = currentYear - 10; y <= currentYear + 10; y++) {
                cmbYear.addItem(y);
            }
            for (Month m : Month.values()) {
                cmbMonth.addItem(new MonthItem(m));
            }
            cmbYear.setSelectedItem(showingMonth.getYear());
            cmbMonth.setSelectedIndex(showingMonth.getMonthValue() - 1);

            cmbYear.addActionListener(e -> updateMonthYearFromSelectors());
            cmbMonth.addActionListener(e -> updateMonthYearFromSelectors());

            prev.addActionListener(e -> {
                showingMonth = showingMonth.minusMonths(1);
                refreshMonth();
            });
            next.addActionListener(e -> {
                showingMonth = showingMonth.plusMonths(1);
                refreshMonth();
            });
            top.add(prev, BorderLayout.WEST);
            top.add(selectors, BorderLayout.CENTER);
            top.add(next, BorderLayout.EAST);

            JPanel center = new JPanel(new BorderLayout(0, 6));
            JPanel header = new JPanel(new GridLayout(1, 7, 4, 4));
            for (String d : DOW) {
                JLabel day = new JLabel(d, SwingConstants.CENTER);
                day.setFont(UITheme.BODY_FONT.deriveFont(java.awt.Font.BOLD));
                header.add(day);
            }
            center.add(header, BorderLayout.NORTH);
            center.add(gridDays, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new GridLayout(1, 3, 8, 0));
            JButton today = UITheme.secondaryButton("Hoy");
            JButton cancel = UITheme.secondaryButton("Cancelar");
            JButton accept = UITheme.primaryButton("Aceptar");
            today.addActionListener(e -> {
                selectedDate = LocalDate.now();
                showingMonth = YearMonth.from(selectedDate);
                refreshMonth();
            });
            cancel.addActionListener(e -> {
                pickedDate = null;
                dispose();
            });
            accept.addActionListener(e -> {
                pickedDate = selectedDate;
                dispose();
            });
            bottom.add(today);
            bottom.add(cancel);
            bottom.add(accept);

            add(top, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);
            add(bottom, BorderLayout.SOUTH);

            refreshMonth();
            pack();
            setLocationRelativeTo(owner);
        }

        private void refreshMonth() {
            String month = showingMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "CO"));
            lblMonth.setText(Character.toUpperCase(month.charAt(0)) + month.substring(1) + " " + showingMonth.getYear());
            cmbYear.setSelectedItem(showingMonth.getYear());
            cmbMonth.setSelectedIndex(showingMonth.getMonthValue() - 1);

            gridDays.removeAll();
            LocalDate first = showingMonth.atDay(1);
            int leading = mapMondayFirst(first.getDayOfWeek());
            int daysInMonth = showingMonth.lengthOfMonth();

            for (int i = 0; i < leading; i++) {
                gridDays.add(new JLabel(""));
            }

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = showingMonth.atDay(day);
                JButton b = new JButton(String.valueOf(day));
                b.setFont(UITheme.BODY_FONT);
                if (date.equals(selectedDate)) {
                    b.setBackground(UITheme.ACCENT);
                    b.setForeground(java.awt.Color.WHITE);
                    b.setOpaque(true);
                    b.setBorderPainted(false);
                }
                b.addActionListener(e -> {
                    selectedDate = date;
                    refreshMonth();
                });
                gridDays.add(b);
            }

            int filled = leading + daysInMonth;
            for (int i = filled; i < 42; i++) {
                gridDays.add(new JLabel(""));
            }
            gridDays.revalidate();
            gridDays.repaint();
        }

        private void updateMonthYearFromSelectors() {
            Integer year = (Integer) cmbYear.getSelectedItem();
            MonthItem month = (MonthItem) cmbMonth.getSelectedItem();
            if (year == null || month == null) {
                return;
            }
            YearMonth target = YearMonth.of(year, month.month.getValue());
            if (!target.equals(showingMonth)) {
                showingMonth = target;
                refreshMonth();
            }
        }

        private int mapMondayFirst(DayOfWeek dow) {
            int val = dow.getValue();
            return val == 7 ? 6 : val - 1;
        }

        private static LocalDate pickDate(Component parent, LocalDate initialDate) {
            Window owner = javax.swing.SwingUtilities.getWindowAncestor(parent);
            if (owner == null) {
                owner = new Frame();
            }
            DatePickerDialog dlg = new DatePickerDialog(owner, initialDate);
            dlg.setVisible(true);
            return dlg.pickedDate;
        }
    }

    public static LocalDate showPicker(Component parent, LocalDate initialDate) {
        return DatePickerDialog.pickDate(parent, initialDate);
    }

    private static final class MonthItem {
        private final Month month;

        private MonthItem(Month month) {
            this.month = month;
        }

        @Override
        public String toString() {
            String name = month.getDisplayName(TextStyle.FULL, new Locale("es", "CO"));
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }
}
