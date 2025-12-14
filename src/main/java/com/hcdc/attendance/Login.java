package com.hcdc.attendance;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login {
    public static void showLogin(JFrame parent, SQLDatabase model) {
        JDialog dlg = new JDialog(parent, "Login", true);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(20);
        // Enforce numeric-only and max 8 digits for ID
        javax.swing.text.AbstractDocument idDoc = (javax.swing.text.AbstractDocument) idField.getDocument();
        idDoc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
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
        JPasswordField pwField = new JPasswordField(20);

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("ID Number:"), gbc);
        gbc.gridx=1; gbc.gridy=0; panel.add(idField, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; gbc.gridy=1; panel.add(pwField, gbc);

        JButton btnLogin = new JButton("Login");
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER; panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword());
            if (id.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please enter both ID Number and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!id.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(dlg, "Error id number", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User user = model.authenticateUser(id, pw);
            if (user != null) {
                dlg.dispose();
                parent.dispose();
                GUI view = new GUI();
                view.setUsername(user.getUsername());
                new AttendancePresenter(model, view);
            } else {
                JOptionPane.showMessageDialog(dlg, "Invalid ID Number or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                pwField.setText("");
            }
        });

        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }
}
