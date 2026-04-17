package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChoiceTest {

    @Test
    public void testChoiceSettersAndGetters() {
        Choice choice = new Choice();
        Question question = new Question();
        
        choice.setChoiceText("Option A");
        choice.setCorrect(true);
        choice.setQuestion(question);
        
        assertEquals("Option A", choice.getChoiceText());
        assertTrue(choice.isCorrect());
        assertEquals(question, choice.getQuestion());
        assertNull(choice.getId());
    }
}
