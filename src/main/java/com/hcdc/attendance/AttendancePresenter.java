package com.hcdc.attendance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AttendancePresenter {
    private SQLDatabase model;
    private GUI view;

    public AttendancePresenter(SQLDatabase model, GUI view) {
        this.model = model;
        this.view = view;
        attachListeners();
        refreshTable();
        view.show();
    }

    private void attachListeners() {
        view.addAddListener(e -> addAttendance());
        view.addDeleteListener(e -> deleteSelected());
        view.addImportListener(e -> importFromFile());
        view.addExportListener(e -> exportCSV());
        view.addSettingsListener(e -> showSettings());
        view.addClearListener(e -> view.clearInputs());
        // When Student ID entry completes (8 digits), auto-fill full name if user exists
        view.addStudentIdCompleteListener(e -> {
            String sid = view.getStudentId();
            if (sid == null || sid.isEmpty()) return;
            User u = model.getUserByUsername(sid);
            if (u != null) {
                view.setFullName(u.getFullName());
                view.focusFullName();
            } else {
                // show concise error per UI guidelines
                view.showError("Error id number", "Error id number");
                view.setFullName("");
                view.focusFullName();
            }
        });
    }

    private void addAttendance() {
        // Step 2: Teacher Inputs Data
        String sid = view.getStudentId();
        String name = view.getFullName();
        String status = view.getStatus();
        
        // Step 3: Validate Input
        if (sid.isEmpty() || name.isEmpty() || status == null || status.isEmpty()) {
            // If INVALID: Show Warning: Missing Data
            view.showWarning("Missing Data", 
                "Please fill in all required fields:\n" +
                (sid.isEmpty() ? "• Student ID is required\n" : "") +
                (name.isEmpty() ? "• Full Name is required\n" : "") +
                (status == null || status.isEmpty() ? "• Status is required" : ""));
            return; // Go back to Teacher Input
        }

        // Validate HCDC Student ID format: exactly 8 digits
        if (!sid.matches("\\d{8}")) {
            view.showError("Error id number", "Error id number");
            return;
        }
        
        // If VALID: Proceed to Insert Record
        // Step 4: Insert Record
        Attendance a = new Attendance(sid, name, status);
        if (model.addAttendance(a)) {
            view.showInfo("Success", "Attendance record has been saved successfully!");
            view.clearInputs();
            // Step 5: Refresh Table View
            refreshTable();
        } else {
            view.showError("Database Error", "Failed to save attendance record. Please try again.");
        }
    }

    private void deleteSelected() {
        // B. Delete Record
        int id = view.getSelectedRecordId();
        if (id == -1) {
            view.showWarning("No Selection", "Please select a record from the table to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            view.getFrame(), 
            "Are you sure you want to delete record ID " + id + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove selected row from the table
            if (model.deleteById(id)) {
                view.showInfo("Success", "Record has been deleted successfully.");
                // Refresh table again
                refreshTable();
            } else {
                view.showError("Delete Failed", "Failed to delete the record. Please try again.");
            }
        }
    }

    private void refreshTable() {
        // Display updated list of records
        view.clearTable();
        List<Attendance> list = model.getAllAttendances();
        int displayId = 1;
        if (list.isEmpty()) {
            // The view will be empty, which is fine.
        } else {
            for (Attendance a : list) {
                // Requirement #4: Auto-renumbering. The display Number is sequential (1..n).
                // Pass DB id in position 1 so the view can use it for deletes.
                view.addRow(new Object[] { 
                    displayId++,              // Display Number (1,2,3,...)
                    a.getId(),               // DB id (hidden column)
                    a.getStudentId(), 
                    a.getFullName(), 
                    a.getStatus(), 
                    a.getTimestamp() 
                });
            }
        }
    }

    private void importFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Attendance (CSV or Excel)");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV or Excel", "csv", "xlsx", "xls"));

        int result = chooser.showOpenDialog(view.getFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            int count = 0;
            String name = file.getName().toLowerCase();
            if (name.endsWith(".csv")) {
                // Parse CSV
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                    String line;
                    boolean first = true;
                    while ((line = br.readLine()) != null) {
                        if (first) { first = false; continue; } // skip header
                        if (line.trim().isEmpty()) continue;
                        // split on commas not inside quotes
                        String[] parts = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                        for (int i = 0; i < parts.length; i++) {
                            String p = parts[i].trim();
                            if (p.startsWith("\"") && p.endsWith("\"")) p = p.substring(1, p.length()-1);
                            parts[i] = p.trim();
                        }
                        String studentId = parts.length > 0 ? parts[0] : "";
                        String fullName = parts.length > 1 ? parts[1] : "";
                        String status = parts.length > 2 ? parts[2] : "Present";
                        if (studentId.isEmpty() || fullName.isEmpty() || status.isEmpty()) continue;
                        Attendance attendance = new Attendance(studentId, fullName, status);
                        if (model.addAttendance(attendance)) count++;
                    }
                    view.showInfo("Import Successful", count + " records have been imported successfully.");
                    refreshTable();
                } catch (IOException e) {
                    view.showError("Import Failed", "Failed to read the CSV file: " + e.getMessage());
                }
            } else {
                // Try Excel import (XLSX)
                try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
                    Sheet sheet = workbook.getSheetAt(0);
                    for (Row row : sheet) {
                        if (row.getRowNum() == 0) continue; // Skip header row
                        String studentId = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim();
                        String fullName = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim();
                        String status = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim();
                        if (studentId.isEmpty() || fullName.isEmpty() || status.isEmpty()) continue;
                        Attendance attendance = new Attendance(studentId, fullName, status);
                        if (model.addAttendance(attendance)) count++;
                    }
                    view.showInfo("Import Successful", count + " records have been imported successfully.");
                    refreshTable();
                } catch (IOException e) {
                    view.showError("Import Failed", "Failed to read the Excel file: " + e.getMessage());
                } catch (Exception e) {
                    view.showError("Import Failed", "An error occurred during import: " + e.getMessage());
                }
            }
        }
    }

    private void showSettings() {
        String[] options = {"Delete All Attended", "Change Password", "Delete Account", "Logout"};
        int choice = JOptionPane.showOptionDialog(
            view.getFrame(),
            "Settings",
            "Dashboard Menu",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) { // Delete All Attended
            deleteAllAttendances();
        } else if (choice == 1) { // Change Password
            changePassword();
        } else if (choice == 2) { // Delete Account
            deleteAccount();
        } else if (choice == 3) { // Logout
            view.getFrame().dispose();
            // Relaunch the homepage
            new Homepage(model).setVisible(true);
        }
    }

    private void deleteAccount() {
        String username = view.getLoggedInUsername();
        if (username == null || username.isEmpty()) {
            view.showError("Not Logged In", "No user is currently logged in.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            view.getFrame(),
            "Are you sure you want to DELETE your account? This action cannot be undone.",
            "Confirm Delete Account",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (model.deleteUser(username)) {
                view.showInfo("Account Deleted", "Your account has been deleted. The application will return to the homepage.");
                view.getFrame().dispose();
                new Homepage(model).setVisible(true);
            } else {
                view.showError("Delete Failed", "Failed to delete account. Please try again.");
            }
        }
    }

    private void deleteAllAttendances() {
        int confirm = JOptionPane.showConfirmDialog(
            view.getFrame(),
            "Are you sure you want to DELETE ALL attendance records? This action cannot be undone.",
            "Confirm Delete All",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (model.deleteAllAttendances()) {
                view.showInfo("Success", "All attendance records have been deleted.");
                refreshTable();
            } else {
                view.showError("Delete Failed", "Failed to delete attendance records. Please try again.");
            }
        }
    }

    private void changePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
            "Old Password:", oldPasswordField,
            "New Password:", newPasswordField,
            "Confirm Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(
            view.getFrame(),
            message,
            "Change Password",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                view.showError("Validation Error", "All fields are required.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                view.showError("Validation Error", "New passwords do not match.");
                return;
            }

            if (newPassword.length() < 6) {
                view.showError("Validation Error", "New password must be at least 6 characters long.");
                return;
            }

            // Update password in database (requires implementation in SQLDatabase)
            if (model.updateUserPassword(view.getLoggedInUsername(), oldPassword, newPassword)) {
                view.showInfo("Success", "Password changed successfully.");
            } else {
                view.showError("Change Password Failed", "Failed to change password. Check your old password.");
            }
        }
    }

    private void exportCSV() {
        // A. Export to CSV
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Attendance Data as CSV");
        chooser.setSelectedFile(new File("attendance_export.csv"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = chooser.showSaveDialog(view.getFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Ensure .csv extension
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            // Convert table data to CSV format
            if (model.exportToCSV(file)) {
                // Save CSV File
                view.showInfo("Export Successful", 
                    "Attendance data has been exported successfully to:\n" + file.getAbsolutePath());
            } else {
                view.showError("Export Failed", "Failed to export data to CSV. Please try again.");
            }
        }
    }
}
