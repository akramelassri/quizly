package com.example.quizly.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {

    @Test
    public void testQuestionSettersAndGetters() {
        Question question = new Question();
        Quiz quiz = new Quiz();
        
        question.setQuestionText("What is 2+2?");
        question.setQuiz(quiz);
        
        assertEquals("What is 2+2?", question.getQuestionText());
        assertEquals(quiz, question.getQuiz());
        assertNull(question.getId());
    }
}
