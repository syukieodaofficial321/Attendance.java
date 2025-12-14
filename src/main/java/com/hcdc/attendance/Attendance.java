package com.hcdc.attendance;

/**
 * Attendance record - small POJO used by the app.
 */
public class Attendance {
    private int id;
    private final String studentId;
    private final String fullName;
    private final String status;
    private final String timestamp;

    public Attendance(int id, String studentId, String fullName, String status, String timestamp) {
        this.id = id;
        this.studentId = studentId;
        this.fullName = fullName;
        this.status = status;
        this.timestamp = timestamp;
    }

 public Attendance(String studentId, String fullName, String status, String timestamp) {
        this(0, studentId, fullName, status, timestamp);
    }

    public Attendance(String studentId, String fullName, String status) {
        this(0, studentId, fullName, status, defaultTimestamp());
    }

    // Use 12-hour timestamp format for display (e.g., 2025-12-12 1:05 PM)
    private static String defaultTimestamp() {
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        return java.time.LocalDateTime.now().format(fmt);
    }

    public Attendance(String studentId, String fullName, String status, boolean useDefaultTimestamp) {
        this(0, studentId, fullName, status, useDefaultTimestamp ? defaultTimestamp() : java.time.LocalDateTime.now().toString());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
}
 
