package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerTest {

    @Test
    public void testAnswerSettersAndGetters() {
        Answer answer = new Answer();
        SessionParticipant participant = new SessionParticipant();
        Question question = new Question();
        Choice choice = new Choice();
        LocalDateTime now = LocalDateTime.now();
        
        answer.setParticipant(participant);
        answer.setQuestion(question);
        answer.setChoice(choice);
        answer.setWager(100);
        answer.setSubmittedAt(now);
        
        assertEquals(participant, answer.getParticipant());
        assertEquals(question, answer.getQuestion());
        assertEquals(choice, answer.getChoice());
        assertEquals(100, answer.getWager());
        assertEquals(now, answer.getSubmittedAt());
        assertNull(answer.getId());
    }
}
