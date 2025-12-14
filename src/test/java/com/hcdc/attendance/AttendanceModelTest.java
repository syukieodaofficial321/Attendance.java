package com.hcdc.attendance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AttendanceModelTest {

    @Test
    public void testAddAndRetrieveAttendance() {
        SQLDatabase model = new SQLDatabase();
        Attendance a = new Attendance("12345", "Test Student", "Present");
        boolean added = model.addAttendance(a);
        assertTrue(added);
        assertFalse(model.getAllAttendances().isEmpty());
    }
}
