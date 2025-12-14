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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
 * Login Dialog for user authentication
 */
public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignUp;
    private boolean loginSuccessful = false;
    private User loggedInUser = null;

    public LoginDialog(JFrame parent) {
        super(parent, "Login - CET Student Attendance", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginSuccessful = false;
            }
        });
        initializeUI();
    }

    private void initializeUI() {
        // Set to full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false); // Keep window decorations

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 250));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Login to Attendance System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        headerPanel.setPreferredSize(new Dimension(screenSize.width, 80));

        // Center panel to hold the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerGbc.anchor = GridBagConstraints.CENTER;

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        formPanel.setPreferredSize(new Dimension(500, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsername.setForeground(new Color(60, 60, 60));
        formPanel.add(lblUsername, gbc);
        
        txtUsername = createStyledTextField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtUsername, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(new Color(60, 60, 60));
        formPanel.add(lblPassword, gbc);
        
        txtPassword = createStyledPasswordField(20);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtPassword, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btnLogin = createStyledButton("Login", new Color(46, 125, 50));
        btnSignUp = createStyledButton("Sign Up", new Color(25, 118, 210));
        
        btnPanel.add(btnLogin);
        btnPanel.add(btnSignUp);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(btnPanel, gbc);

        // Add form panel to center panel
        centerPanel.add(formPanel, centerGbc);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Enter key listener for login
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
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

    public void setLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void setSignUpListener(ActionListener listener) {
        btnSignUp.addActionListener(listener);
    }

    public void performLogin() {
        btnLogin.doClick();
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void setLoginSuccessful(boolean success) {
        this.loginSuccessful = success;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }
}