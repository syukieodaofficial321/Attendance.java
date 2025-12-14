package com.hcdc.attendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLDatabase {

    // SQLite database file
    private static final String DB_FILE = "attendance.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    private Connection conn;

    public SQLDatabase() {
        connect();
        if (conn != null) {
            initializeTables();
        } else {
            System.err.println("Warning: Database connection failed. Some features may not work.");
        }
    }
    
    // Initialize database tables if they don't exist
    private void initializeTables() {
        if (conn == null) {
            System.err.println("Cannot initialize tables: Database connection is null.");
            return;
        }
        
        try {
            // Create attendance table (SQLite syntax)
            String attendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id TEXT NOT NULL," +
                    "full_name TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "timestamp TEXT NOT NULL" +
                    ")";
            update(attendanceTable);
            System.out.println("Attendance table initialized.");
            
            // Create users table (SQLite syntax)
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "email TEXT UNIQUE," +
                    "password TEXT NOT NULL," +
                    "full_name TEXT NOT NULL," +
                    "role TEXT NOT NULL DEFAULT 'USER'," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            update(usersTable);
            System.out.println("Users table initialized.");
            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Connect to SQLite database
    private void connect() {
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database successfully: " + DB_FILE);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Please check your dependencies.");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            conn = null;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            conn = null;
        }
    }
    
    // Check if database is connected
    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // ✅ Close connection
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }

    // Query method
    public ResultSet query(String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Database connection is null. Please check your database file.");
        }
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    // Update/insert/delete method
    public int update(String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Database connection is null. Please check your database file.");
        }
        Statement stmt = conn.createStatement();
        return stmt.executeUpdate(sql);
    }

    // Attendance methods
    public boolean addAttendance(Attendance a) {
        if (conn == null) {
            System.err.println("Cannot add attendance: Database connection is null.");
            return false;
        }
        try {
            String sql = "INSERT INTO attendance(student_id, full_name, status, timestamp) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, a.getStudentId());
            pstmt.setString(2, a.getFullName());
            pstmt.setString(3, a.getStatus());
            pstmt.setString(4, a.getTimestamp());
            int affected = pstmt.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            System.err.println("Error adding attendance: " + e.getMessage());
            return false;
        }
    }

    public List<Attendance> getAllAttendances() {
        List<Attendance> list = new ArrayList<>();
        if (conn == null) {
            System.err.println("Cannot get attendances: Database connection is null.");
            return list;
        }
        try {
            String sql = "SELECT id, student_id, full_name, status, timestamp FROM attendance ORDER BY id DESC";
            ResultSet rs = query(sql);
            while (rs.next()) {
                Attendance a = new Attendance(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("status"),
                    rs.getString("timestamp")
                );
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendances: " + e.getMessage());
        }
        return list;
    }

    public boolean deleteById(int id) {
        if (conn == null) {
            System.err.println("Cannot delete attendance: Database connection is null.");
            return false;
        }
        try {
            String sql = "DELETE FROM attendance WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            System.err.println("Error deleting attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete all attendance records. Returns true if operation succeeded.
     */
    public boolean deleteAllAttendances() {
        if (conn == null) {
            System.err.println("Cannot delete attendances: Database connection is null.");
            return false;
        }
        try {
            String sql = "DELETE FROM attendance";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            // Reset AUTOINCREMENT sequence for attendance (if present)
            try {
                stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='attendance'");
            } catch (SQLException ignore) {
                // Older SQLite versions or setups may not have sqlite_sequence accessible; ignore
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting all attendances: " + e.getMessage());
            return false;
        }
    }

    public boolean exportToCSV(java.io.File file) {
        if (conn == null) {
            System.err.println("Cannot export CSV: Database connection is null.");
            return false;
        }
        try {
            String sql = "SELECT id, student_id, full_name, status, timestamp FROM attendance ORDER BY id ASC";
            ResultSet rs = query(sql);
            java.io.FileWriter fw = new java.io.FileWriter(file);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            
            bw.write("id,student_id,full_name,status,timestamp");
            bw.newLine();
            
            while (rs.next()) {
                bw.write(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\"",
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("status"),
                    rs.getString("timestamp")));
                bw.newLine();
            }
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }

    // User authentication methods
    public User authenticateUser(String username, String password) {
        if (conn == null) {
            System.err.println("Cannot authenticate user: Database connection is null.");
            return null;
        }
        try {
            String sql = "SELECT id, username, email, password, full_name, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Return a user by username (ID) without password check, or null if not found.
     */
    public User getUserByUsername(String username) {
        if (conn == null) return null;
        try {
            String sql = "SELECT id, username, email, password, full_name, role FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public boolean userExists(String username, String email) {
        if (conn == null) {
            System.err.println("Cannot check user existence: Database connection is null.");
            return false;
        }
        try {
            String sql;
            if (email != null) {
                sql = "SELECT COUNT(*) as count FROM users WHERE username = ? OR email = ?";
            } else {
                sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            if (email != null) {
                pstmt.setString(2, email);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    public boolean registerUser(User user) {
        if (conn == null) {
            System.err.println("Cannot register user: Database connection is null.");
            return false;
        }
        try {
            String sql = "INSERT INTO users(username, email, password, full_name, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getRole() != null ? user.getRole() : "USER");
            int affected = pstmt.executeUpdate();
            System.out.println("User registered successfully: " + user.getUsername());
            return affected == 1;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserPassword(String username, String oldPassword, String newPassword) {
        if (conn == null) {
            System.err.println("Cannot update password: Database connection is null.");
            return false;
        }
        try {
            // First verify that the old password is correct
            String verifySql = "SELECT id FROM users WHERE username = ? AND password = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
            verifyStmt.setString(1, username);
            verifyStmt.setString(2, oldPassword);
            ResultSet rs = verifyStmt.executeQuery();
            
            if (!rs.next()) {
                return false; // Old password doesn't match
            }
            
            // Update the password
            String updateSql = "UPDATE users SET password = ? WHERE username = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, username);
            int affected = updateStmt.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a user account by username. Returns true if one row was removed.
     */
    public boolean deleteUser(String username) {
        if (conn == null) {
            System.err.println("Cannot delete user: Database connection is null.");
            return false;
        }
        try {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            int affected = pstmt.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
