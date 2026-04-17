package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

public class QuizTest {

    @Test
    public void testQuizSettersAndGetters() {
        Quiz quiz = new Quiz();
        Teacher teacher = new Teacher("Jane", "jane@example.com");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        quiz.setTitle("Math Quiz");
        quiz.setDescription("Basic math questions");
        quiz.setSubject("Math");
        quiz.setTeacher(teacher);
        quiz.setCreatedAt(now);
        
        assertEquals("Math Quiz", quiz.getTitle());
        assertEquals("Basic math questions", quiz.getDescription());
        assertEquals("Math", quiz.getSubject());
        assertEquals(teacher, quiz.getTeacher());
        assertEquals(now, quiz.getCreatedAt());
        assertNull(quiz.getId());
    }
}
