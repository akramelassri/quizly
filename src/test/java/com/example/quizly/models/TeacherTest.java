package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TeacherTest {

    @Test
    public void testTeacherConstructorAndGetters() {
        Teacher teacher = new Teacher("John Doe", "john@example.com");
        
        assertEquals("John Doe", teacher.getName());
        assertEquals("john@example.com", teacher.getEmail());
        assertNull(teacher.getId());
    }

    @Test
    public void testTeacherSetters() {
        Teacher teacher = new Teacher();
        teacher.setName("Jane Doe");
        teacher.setEmail("jane@example.com");
        
        assertEquals("Jane Doe", teacher.getName());
        assertEquals("jane@example.com", teacher.getEmail());
    }
}
