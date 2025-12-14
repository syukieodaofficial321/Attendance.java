package com.hcdc.attendance;

import javax.swing.JOptionPane;

/**
 * Main entry point for CET Student Attendance System
 * Requires user authentication before accessing the main application
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Initialize database model
            SQLDatabase model = new SQLDatabase();
            
            // Check database connection
            if (!model.isConnected()) {
                JOptionPane.showMessageDialog(null,
                    "Database Connection Failed!\n\n" +
                    "Please ensure:\n" +
                    "1. SQLite JDBC driver is available\n" +
                    "2. Database file 'attendance.db' is accessible\n\n" +
                    "The application will continue but database features may not work.",
                    "Database Connection Error",
                    JOptionPane.WARNING_MESSAGE);
            }
            
            // Create and show the homepage (separate login/register handled by modal dialogs)
            Homepage homepage = new Homepage(model);
            homepage.setVisible(true);
        });
    }
}
