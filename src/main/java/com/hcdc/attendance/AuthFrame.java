package com.hcdc.attendance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.Year;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AuthFrame extends JFrame {

    private final SQLDatabase model;
    private javax.swing.JDialog activeDialog = null;
    private final JTextField loginIdField = new JTextField(20);
    private final JPasswordField loginPasswordField = new JPasswordField(20);
    private final JTextField regIdField = new JTextField(20);
    private final JTextField regNameField = new JTextField(20);
    private final JPasswordField regPasswordField = new JPasswordField(20);
    private final JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");

    public AuthFrame(SQLDatabase model) {
        this.model = model;
        Theme.applyGlobal();
        setTitle("Attendance System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Main container
        Container contentPane = getContentPane();
        contentPane.setBackground(Theme.BACKGROUND);
        contentPane.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Keep header minimal on homepage. The dashboard (`GUI`) will show Welcome + greeting.
        JLabel title = new JLabel("  Attendance System");
        title.setFont(Theme.HEADER_FONT);
        title.setForeground(Color.WHITE);

        JPopupMenu optionsMenu = new JPopupMenu();
        JMenuItem loginItem = new JMenuItem("Login");
        JMenuItem registerItem = new JMenuItem("Register");
        optionsMenu.add(loginItem);
        optionsMenu.add(registerItem);

        JLabel optionsLabel = new JLabel("⋮");
        optionsLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 28));
        optionsLabel.setForeground(Color.WHITE);
        optionsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                optionsMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        headerPanel.add(optionsLabel, BorderLayout.WEST);
        headerPanel.add(title, BorderLayout.CENTER);

        // center is left empty for homepage - login/register will open as dialogs
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(new JLabel(""));

        // Footer
        String currentYear = String.valueOf(Year.now().getValue());
        JLabel footerLabel = new JLabel(
            "<html><div style='text-align: center;'>Attendance System<br>" +
            "Designed & Developed by: Syukie Oda and Hilario Serencio<br>" +
            "All rights reserved © " + currentYear + "</div></html>",
            SwingConstants.CENTER
        );
        footerLabel.setFont(Theme.PRIMARY_FONT.deriveFont(11f));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to frame
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(centerWrapper, BorderLayout.CENTER);
        contentPane.add(footerLabel, BorderLayout.SOUTH);

        // Action Listeners - open login/register as modal dialogs (not embedded on homepage)
        loginItem.addActionListener(e -> {
            JDialog dlg = new JDialog(this, "Login", true);
            dlg.getContentPane().add(createLoginPanel());
            dlg.pack();
            dlg.setLocationRelativeTo(this);
            activeDialog = dlg;
            dlg.setVisible(true);
            activeDialog = null;
        });

        registerItem.addActionListener(e -> {
            JDialog dlg = new JDialog(this, "Register", true);
            dlg.getContentPane().add(createRegisterPanel());
            dlg.pack();
            dlg.setLocationRelativeTo(this);
            activeDialog = dlg;
            dlg.setVisible(true);
            activeDialog = null;
        });
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) return "Good morning";
        if (hour >= 12 && hour < 18) return "Good afternoon";
        return "Good evening";
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(400, 300));
        panel.setBorder(BorderFactory.createTitledBorder("Login"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("ID Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(loginIdField, gbc);
        
        // Enforce numeric-only input for ID field and max length 8
        AbstractDocument doc = (AbstractDocument) loginIdField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() + filtered.length() <= 8) {
                    super.insertString(fb, offset, filtered, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String filtered = text.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() - length + filtered.length() <= 8) {
                    super.replace(fb, offset, length, filtered, attrs);
                }
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(loginPasswordField, gbc);

        JButton loginButton = new JButton("Login");
        Theme.styleButton(loginButton);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        loginIdField.addActionListener(e -> loginPasswordField.requestFocusInWindow());
        loginPasswordField.addActionListener(e -> handleLogin());

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(400, 300));
        panel.setBorder(BorderFactory.createTitledBorder("Register"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("ID Number (8 digits):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(regIdField, gbc);
        
        // Enforce numeric-only input for ID field
        javax.swing.text.AbstractDocument doc = (javax.swing.text.AbstractDocument) regIdField.getDocument();
        doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() + filtered.length() <= 8) {
                    super.insertString(fb, offset, filtered, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (text == null) return;
                String filtered = text.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() - length + filtered.length() <= 8) {
                    super.replace(fb, offset, length, filtered, attrs);
                }
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(regNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(regPasswordField, gbc);

        gbc.gridx = 1; gbc.gridy = 3; panel.add(showPasswordCheckbox, gbc);

        JButton registerButton = new JButton("Register");
        Theme.styleButton(registerButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                regPasswordField.setEchoChar((char) 0);
            } else {
                regPasswordField.setEchoChar('•');
            }
        });

        registerButton.addActionListener(e -> handleRegister());

        return panel;
    }

    private void handleLogin() {
        String id = loginIdField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both ID Number and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Enforce exact 8-digit HCDC ID
        if (!id.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "Error id number", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = model.authenticateUser(id, password);
        if (user != null) {
            // Login successful, open dashboard
            if (activeDialog != null) activeDialog.dispose();
            this.dispose();
            GUI view = new GUI();
            view.setUsername(user.getUsername());
            new AttendancePresenter(model, view);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID Number or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            loginPasswordField.setText("");
        }
    }

    private void handleRegister() {
        String id = regIdField.getText().trim();
        String fullName = regNameField.getText().trim();
        String password = new String(regPasswordField.getPassword());

        // Validation
        if (id.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // HCDC ID Format Validation: Must be exactly 8 digits
        if (!id.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "Error id number", "Registration Error", JOptionPane.ERROR_MESSAGE);
            regIdField.setText("");
            regIdField.requestFocusInWindow();
            return;
        }

        if (model.userExists(id, null)) {
            JOptionPane.showMessageDialog(this, "An account with this ID Number already exists.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(id, null, password, fullName);
        if (model.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Close registration dialog (if open) and clear fields
            if (activeDialog != null) activeDialog.dispose();
            regIdField.setText("");
            regNameField.setText("");
            regPasswordField.setText("");
            loginIdField.setText(id);
            loginPasswordField.requestFocusInWindow();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create account. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}