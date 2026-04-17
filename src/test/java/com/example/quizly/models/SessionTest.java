package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    @Test
    public void testSessionSettersAndGetters() {
        Session session = new Session();
        Quiz quiz = new Quiz();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        session.setQuiz(quiz);
        session.setJoinCode("123456");
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartedAt(now);
        session.setEndedAt(now);
        
        assertEquals(quiz, session.getQuiz());
        assertEquals("123456", session.getJoinCode());
        assertEquals(SessionStatus.ACTIVE, session.getStatus());
        assertEquals(now, session.getStartedAt());
        assertEquals(now, session.getEndedAt());
        assertNull(session.getId());
    }
}
