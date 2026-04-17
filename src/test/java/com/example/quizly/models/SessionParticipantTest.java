package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class SessionParticipantTest {

    @Test
    public void testSessionParticipantSettersAndGetters() {
        SessionParticipant participant = new SessionParticipant();
        Session session = new Session();
        LocalDateTime now = LocalDateTime.now();
        
        participant.setSession(session);
        participant.setUsername("student1");
        participant.setScore(50);
        participant.setJoinedAt(now);
        
        assertEquals(session, participant.getSession());
        assertEquals("student1", participant.getUsername());
        assertEquals(50, participant.getScore());
        assertEquals(now, participant.getJoinedAt());
        assertNull(participant.getId());
    }
}
