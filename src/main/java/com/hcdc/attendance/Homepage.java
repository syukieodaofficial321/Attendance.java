package com.hcdc.attendance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.time.Year;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

public class Homepage extends JFrame {
    private final SQLDatabase model;

    public Homepage(SQLDatabase model) {
        this.model = model;
        Theme.applyGlobal();
        setTitle("Attendance System - Homepage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.setBackground(Theme.BACKGROUND);

        // Header with three-dots menu (Login / Register)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("  Attendance System");
        title.setFont(Theme.HEADER_FONT);
        title.setForeground(Color.WHITE);

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(Theme.HEADER_FONT.deriveFont(22f));
        welcome.setForeground(Color.WHITE);
        JLabel greeting = new JLabel(getGreeting());
        greeting.setFont(Theme.PRIMARY_FONT.deriveFont(14f));
        greeting.setForeground(Color.WHITE);
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.add(welcome);
        welcomePanel.add(greeting);

        // Right-side panel: greeting and Login/Register button
        JButton authBtn = new JButton("Login / Register");
        authBtn.setFocusable(false);
        JPopupMenu menu = new JPopupMenu();
        JMenuItem login = new JMenuItem("Login");
        JMenuItem register = new JMenuItem("Register");
        menu.add(login);
        menu.add(register);
        authBtn.addActionListener(e -> menu.show(authBtn, 0, authBtn.getHeight()));
        login.addActionListener(e -> Login.showLogin(this, model));
        register.addActionListener(e -> Register.showRegister(this, model));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(greeting);
        rightPanel.add(authBtn);

        header.add(title, BorderLayout.WEST);
        // center filler should be transparent to avoid white bar at top
        JPanel headerCenter = new JPanel();
        headerCenter.setOpaque(false);
        header.add(headerCenter, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        // Footer
        String currentYear = String.valueOf(Year.now().getValue());
        JLabel footer = new JLabel(
            "<html><div style='text-align:center;'>Attendance System<br>Designed & Developed by: Syukie Oda and Hilario Serencio<br>All rights reserved © " + currentYear + "</div></html>",
            SwingConstants.CENTER
        );
        footer.setFont(Theme.PRIMARY_FONT.deriveFont(11f));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Center info
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BACKGROUND);
        JLabel info = new JLabel("Click the options menu (⋮) to Login or Register.");
        info.setFont(Theme.PRIMARY_FONT.deriveFont(16f));
        info.setForeground(Color.DARK_GRAY);

        JLabel quote = new JLabel("\u201C" + QuoteOfTheDay.getQuote() + "\u201D");
        quote.setFont(Theme.PRIMARY_FONT.deriveFont(14f));
        quote.setForeground(Color.GRAY);
        quote.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0; gbcCenter.gridy = 0; gbcCenter.insets = new Insets(8,8,8,8);
        center.add(info, gbcCenter);
        gbcCenter.gridy = 1;
        center.add(quote, gbcCenter);

        c.add(header, BorderLayout.NORTH);
        c.add(center, BorderLayout.CENTER);
        c.add(footer, BorderLayout.SOUTH);
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) return "Good morning";
        if (hour >= 12 && hour < 18) return "Good afternoon";
        return "Good evening";
    }
}
