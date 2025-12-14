package com.hcdc.attendance;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Register {
    public static void showRegister(JFrame parent, SQLDatabase model) {
        JDialog dlg = new JDialog(parent, "Register", true);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(8);
        JTextField nameField = new JTextField(20);
        JPasswordField pwField = new JPasswordField(20);
        JCheckBox showPw = new JCheckBox("Show Password");

        // Enforce numeric-only input and max length 8 for ID field
        AbstractDocument idDoc = (AbstractDocument) idField.getDocument();
        idDoc.setDocumentFilter(new DocumentFilter() {
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

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("ID Number (8 digits):"), gbc);
        gbc.gridx=1; gbc.gridy=0; panel.add(idField, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx=1; gbc.gridy=1; panel.add(nameField, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; gbc.gridy=2; panel.add(pwField, gbc);
        gbc.gridx=1; gbc.gridy=3; panel.add(showPw, gbc);

        JButton btnRegister = new JButton("Register");
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER; panel.add(btnRegister, gbc);

        showPw.addActionListener(e -> {
            if (showPw.isSelected()) pwField.setEchoChar((char)0); else pwField.setEchoChar('•');
        });

        btnRegister.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String pw = new String(pwField.getPassword());
            if (id.isEmpty() || name.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "All fields are required.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!id.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(dlg, "Error id number", "Registration Error", JOptionPane.ERROR_MESSAGE);
                idField.requestFocusInWindow();
                return;
            }
            if (model.userExists(id, null)) {
                JOptionPane.showMessageDialog(dlg, "An account with this ID Number already exists.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User newUser = new User(id, null, pw, name);
            if (model.registerUser(newUser)) {
                JOptionPane.showMessageDialog(dlg, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to create account. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }
}
