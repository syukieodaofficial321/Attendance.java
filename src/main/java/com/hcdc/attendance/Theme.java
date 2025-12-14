package com.hcdc.attendance;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class Theme {
    // Palette
    public static final Color ACCENT = new Color(0x2E8B57);
    public static final Color ACCENT_DARK = ACCENT.darker();
    public static final Color BACKGROUND = new Color(0xF5F7F9);
    public static final Color PANEL_BG = new Color(0xF7FBF8);
    public static final Color HEADER_BG = new Color(0x256B4A);
    public static final Color BUTTON_BG = ACCENT;
    public static final Color BUTTON_FG = Color.white;
    public static final Color TABLE_EVEN = Color.white;
    public static final Color TABLE_ODD = new Color(0xEEF6F1);

    // Fonts
    public static final Font PRIMARY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font HEADER_FONT = PRIMARY_FONT.deriveFont(Font.BOLD, 18f);

    // Apply a few global UI defaults (font etc.)
    public static void applyGlobal() {
        try {
            UIManager.put("Label.font", new FontUIResource(PRIMARY_FONT));
            UIManager.put("Button.font", new FontUIResource(PRIMARY_FONT));
            UIManager.put("TextField.font", new FontUIResource(PRIMARY_FONT));
            UIManager.put("Table.font", new FontUIResource(PRIMARY_FONT));
            UIManager.put("TableHeader.font", new FontUIResource(PRIMARY_FONT.deriveFont(Font.BOLD)));
            UIManager.put("ComboBox.font", new FontUIResource(PRIMARY_FONT));
        } catch (Exception ignored) {}
    }

    public static void styleButton(JButton b) {
        b.setBackground(BUTTON_BG);
        b.setForeground(BUTTON_FG);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
    }

    public static void stylePanel(JComponent p) {
        p.setBackground(PANEL_BG);
    }

    public static void styleHeader(JPanel header, JLabel title, JButton logout, JLabel userLabel) {
        header.setBackground(HEADER_BG);
        title.setForeground(Color.white);
        title.setFont(HEADER_FONT);
        if (userLabel != null) {
            userLabel.setForeground(Color.white);
            userLabel.setFont(PRIMARY_FONT);
        }
        if (logout != null) {
            logout.setBackground(Color.white);
            logout.setForeground(HEADER_BG.darker());
            logout.setFocusPainted(false);
        }
    }

    public static void styleTableHeader(JTable table) {
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(Color.white);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
    }
}
