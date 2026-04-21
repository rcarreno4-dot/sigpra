/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Integrador.ui;

import Integrador.*;
import Integrador.model.*;
import Integrador.session.AppSession;

import java.awt.BorderLayout;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DirectorApprovalFrame extends BaseFrame {
    private final DirectorApprovalDao approvalDao = new DirectorApprovalDao();
    private JTable table;

    public DirectorApprovalFrame() {
        super("Aprobacion de Cierre", "Revision final de practicas que cumplieron horas", Role.DIRECTOR);
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "Solo la directora puede aprobar el cierre final.",
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
        navButton("Aprobaciones", true, () -> { });
        navButton("Docentes", false, () -> goTo(new TeacherRegistrationFrame()));
        navButton("Asignaciones", false, () -> goTo(new PracticeRegistrationFrame(role)));
        navButton("Reportes", false, () -> goTo(new ReportsFrame()));
        navButton("Cerrar sesion", false, this::closeAllAndReturnToLogin);
    }

    private void buildBody() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        table = new JTable();
        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.add(UITheme.subtitleLabel("Practicas pendientes por aprobacion de directora"), BorderLayout.NORTH);
        card.add(UITheme.tableScroll(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        var btnDetail = UITheme.secondaryButton("Ver revision docente");
        btnDetail.addActionListener(e -> showTeacherReview());
        var btnApprove = UITheme.primaryButton("Aprobar cierre");
        btnApprove.addActionListener(e -> processApproval(true));
        var btnReturn = UITheme.secondaryButton("Devolver a seguimiento");
        btnReturn.addActionListener(e -> processApproval(false));
        actions.add(btnDetail);
        actions.add(btnApprove);
        actions.add(btnReturn);

        root.add(card, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        body.add(root, BorderLayout.CENTER);
    }

    private void processApproval(boolean approve) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una practica para continuar.",
                    "Aprobacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            JOptionPane.showMessageDialog(this,
                    "La aprobacion solo puede realizarla una directora con sesion activa.",
                    "Aprobacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long practiceId = Long.parseLong(table.getValueAt(row, 0).toString());
        String obs = JOptionPane.showInputDialog(this,
                approve ? "Observacion de aprobacion (opcional):" : "Motivo para devolver a seguimiento:");
        if (!approve && (obs == null || obs.isBlank())) {
            JOptionPane.showMessageDialog(this,
                    "Debes indicar el motivo para devolver la practica.",
                    "Aprobacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (obs == null) {
            obs = "";
        }

        try {
            if (approve) {
                approvalDao.approveClosure(practiceId, user.id(), obs.trim());
            } else {
                approvalDao.returnToTracking(practiceId, obs.trim());
            }
            reloadTable();
            JOptionPane.showMessageDialog(this,
                    approve ? "Cierre aprobado correctamente." : "Practica devuelta a seguimiento docente.",
                    "Aprobacion",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el estado de la practica.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadTable() {
        AuthenticatedUser user = AppSession.getCurrentUser();
        if (user == null || user.role() != Role.DIRECTOR) {
            table.setModel(emptyModel());
            return;
        }
        try {
            table.setModel(approvalDao.listPendingApprovals(user.id()));
        } catch (SQLException ex) {
            table.setModel(emptyModel());
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la cola de aprobaciones.\nDetalle: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultTableModel emptyModel() {
        return new DefaultTableModel(new String[]{
                "ID Practica",
                "Estudiante",
                "Docente",
                "Periodo",
                "Horas",
                "Validadas",
                "Rechazadas",
                "Pendientes",
                "Observacion docente",
                "Fecha registro"
        }, 0);
    }

    private void showTeacherReview() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una practica para ver su revision docente.",
                    "Revision docente",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String docente = String.valueOf(table.getValueAt(row, 2));
        String horas = String.valueOf(table.getValueAt(row, 4));
        String validadas = String.valueOf(table.getValueAt(row, 5));
        String rechazadas = String.valueOf(table.getValueAt(row, 6));
        String pendientes = String.valueOf(table.getValueAt(row, 7));
        String observacion = String.valueOf(table.getValueAt(row, 8));

        String detail = "Docente asesor: " + docente
                + "\nHoras validadas de practica: " + horas
                + "\nActividades validadas: " + validadas
                + "\nActividades rechazadas: " + rechazadas
                + "\nActividades pendientes: " + pendientes
                + "\nUltima observacion docente: " + observacion;

        JOptionPane.showMessageDialog(this,
                detail,
                "Revision docente",
                JOptionPane.INFORMATION_MESSAGE);
    }
}



