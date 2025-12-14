package com.hcdc.attendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class to initialize the attendance database
 */
public class InitializeDatabase {
    private static final String DB_FILE = "attendance.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    public static void main(String[] args) {
        initializeDatabase();
    }

    public static void initializeDatabase() {
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite driver loaded successfully.");

            // Create connection
            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection created: " + DB_FILE);

            Statement stmt = conn.createStatement();

            // Create attendance table
            String attendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id TEXT NOT NULL," +
                    "full_name TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "timestamp TEXT NOT NULL" +
                    ")";
            stmt.execute(attendanceTable);
            System.out.println("✓ Attendance table created successfully.");

            // Create users table
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "email TEXT UNIQUE," +
                    "password TEXT NOT NULL," +
                    "full_name TEXT NOT NULL," +
                    "role TEXT NOT NULL DEFAULT 'USER'," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(usersTable);
            System.out.println("✓ Users table created successfully.");

            stmt.close();
            conn.close();
            System.out.println("\n✓ Database initialized successfully!");
            System.out.println("File created: " + DB_FILE);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: SQLite JDBC driver not found!");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
