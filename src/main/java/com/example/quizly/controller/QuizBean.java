package com.example.quizly.controller;

import com.example.quizly.models.Choice;
import com.example.quizly.models.Prof;
import com.example.quizly.models.Question;
import com.example.quizly.models.Quiz;
import com.example.quizly.dao.ChoiceDAO;
import com.example.quizly.dao.ProfDAO;
import com.example.quizly.dao.QuestionDAO;
import com.example.quizly.dao.QuizDAO;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

@Named
@ViewScoped
public class QuizBean implements Serializable {

    private Quiz quiz;

    @Inject
    private QuizDAO quizDAO;
    @Inject
    private QuestionDAO questionDAO;
    @Inject
    private ChoiceDAO choiceDAO;
    @Inject
    private ProfDAO profDAO;
    @Inject
    private ProfSession profSession;

    public QuizBean() {
        quiz = new Quiz();
        quiz.setQuestions(new ArrayList<>());
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void addQuestion() {
        Question q = new Question();
        q.setChoices(new ArrayList<>());
        q.setQuiz(quiz);

        // Ensure the list is initialized
        if (quiz.getQuestions() == null) {
            quiz.setQuestions(new ArrayList<>());
        }
        quiz.getQuestions().add(q);
    }

    public void removeQuestion(Question q) {
        if (quiz.getQuestions() != null) {
            quiz.getQuestions().remove(q);
        }
    }

    public void addChoice(Question q) {
        Choice c = new Choice();
        c.setQuestion(q);
        c.setCorrect(false);

        if (q.getChoices() == null) {
            q.setChoices(new ArrayList<>());
        }
        q.getChoices().add(c);
    }

    public void removeChoice(Question q, Choice c) {
        if (q.getChoices() != null) {
            q.getChoices().remove(c);
        }
    }

    public String saveQuiz() {
        quiz.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        System.out.println("------------------------------------------------");
        System.out.println("prof: " + profSession.getEmail());

        // Find the current Prof from DB based on session email
        Prof currentProf = profDAO.findByEmail(profSession.getEmail()).orElse(null);
        if (currentProf == null) {
            // Handle error: not logged in properly or user not found
            return "/login?faces-redirect=true";
        }
        quiz.setTeacher(currentProf);

        // 1. Save standard quiz
        quizDAO.save(quiz);

        // 2. Save all questions and their choices sequentially
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                q.setQuiz(quiz); // Ensure association
                questionDAO.save(q);

                if (q.getChoices() != null) {
                    for (Choice c : q.getChoices()) {
                        c.setQuestion(q); // Ensure association
                        choiceDAO.save(c);
                    }
                }
            }
        }

        return "/prof/dashboard?faces-redirect=true";
    }
}
