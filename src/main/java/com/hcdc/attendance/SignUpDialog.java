package com.hcdc.attendance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Sign Up Dialog for user registration
 */
public class SignUpDialog extends JDialog {
    private JTextField txtUsername, txtEmail, txtFullName;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnSignUp, btnCancel;
    private boolean signUpSuccessful = false;

    public SignUpDialog(JFrame parent) {
        super(parent, "Sign Up - CET Student Attendance", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 245, 250));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Full Name field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFullName.setForeground(new Color(60, 60, 60));
        formPanel.add(lblFullName, gbc);
        
        txtFullName = createStyledTextField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtFullName, gbc);

        // Username field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsername.setForeground(new Color(60, 60, 60));
        formPanel.add(lblUsername, gbc);
        
        txtUsername = createStyledTextField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtUsername, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmail.setForeground(new Color(60, 60, 60));
        formPanel.add(lblEmail, gbc);
        
        txtEmail = createStyledTextField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtEmail, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(new Color(60, 60, 60));
        formPanel.add(lblPassword, gbc);
        
        txtPassword = createStyledPasswordField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtPassword, gbc);

        // Confirm Password field
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConfirmPassword.setForeground(new Color(60, 60, 60));
        formPanel.add(lblConfirmPassword, gbc);
        
        txtConfirmPassword = createStyledPasswordField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtConfirmPassword, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btnSignUp = createStyledButton("Sign Up", new Color(46, 125, 50));
        btnCancel = createStyledButton("Cancel", new Color(158, 158, 158));
        
        btnPanel.add(btnSignUp);
        btnPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(btnPanel, gbc);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 190), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK); // Black text color
        field.setCaretColor(Color.BLACK); // Black caret
        field.setEnabled(true); // Ensure field is enabled
        field.setEditable(true); // Ensure field is editable
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
                ));
                field.setBackground(new Color(248, 250, 255)); // Light blue tint
                field.setForeground(Color.BLACK); // Keep black text when focused
                field.setCaretColor(new Color(70, 130, 180)); // Blue caret when focused
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 190), 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
                field.setBackground(Color.WHITE);
                field.setForeground(Color.BLACK); // Keep black text
                field.setCaretColor(Color.BLACK); // Black caret when not focused
            }
        });
        
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 190), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK); // Black text color
        field.setCaretColor(Color.BLACK); // Black caret
        field.setEnabled(true); // Ensure field is enabled
        field.setEditable(true); // Ensure field is editable
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
                ));
                field.setBackground(new Color(248, 250, 255)); // Light blue tint
                field.setForeground(Color.BLACK); // Keep black text when focused
                field.setCaretColor(new Color(70, 130, 180)); // Blue caret when focused
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 190), 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
                field.setBackground(Color.WHITE);
                field.setForeground(Color.BLACK); // Keep black text
                field.setCaretColor(Color.BLACK); // Black caret when not focused
            }
        });
        
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.darker().darker(), 1),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(bgColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.darker(), 1),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }
            public void mousePressed(MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseReleased(MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
            }
        });
        return btn;
    }

    public void setSignUpListener(ActionListener listener) {
        btnSignUp.addActionListener(listener);
    }

    public void setCancelListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getEmail() {
        return txtEmail.getText().trim();
    }

    public String getFullName() {
        return txtFullName.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public String getConfirmPassword() {
        return new String(txtConfirmPassword.getPassword());
    }

    public void setSignUpSuccessful(boolean success) {
        this.signUpSuccessful = success;
    }

    public boolean isSignUpSuccessful() {
        return signUpSuccessful;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Sign Up Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtUsername.setText("");
        txtEmail.setText("");
        txtFullName.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtFullName.requestFocus();
    }

    public boolean validateFields() {
        if (getFullName().isEmpty() || getUsername().isEmpty() || 
            getEmail().isEmpty() || getPassword().isEmpty()) {
            showError("All fields are required!");
            return false;
        }

        if (getPassword().length() < 6) {
            showError("Password must be at least 6 characters long!");
            return false;
        }

        if (!getPassword().equals(getConfirmPassword())) {
            showError("Passwords do not match!");
            return false;
        }

        if (!getEmail().contains("@") || !getEmail().contains(".")) {
            showError("Please enter a valid email address!");
            return false;
        }

        return true;
    }
}
