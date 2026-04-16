package com.example.quizly.controller;

import com.example.quizly.dao.ChoiceDAO;
import com.example.quizly.dao.QuestionDAO;
import com.example.quizly.dao.QuizDAO;
import com.example.quizly.models.Choice;
import com.example.quizly.models.Question;
import com.example.quizly.models.Quiz;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
@SessionScoped
public class QuestionBean implements Serializable {

    @Inject private QuestionDAO questionDAO;
    @Inject private ChoiceDAO choiceDAO;
    @Inject private QuizDAO quizDAO;

    private Long currentQuizId;
    private Quiz currentQuiz;
    private List<Question> questions;
    private Question currentQuestion = new Question();
    private List<Choice> currentChoices = new ArrayList<>();

    public void loadForQuiz(Long quizId) {
        this.currentQuizId = quizId;
        Optional<Quiz> quiz = quizDAO.findById(quizId);
        quiz.ifPresent(q -> {
            this.currentQuiz = q;
            this.questions = questionDAO.findByQuiz(q);
        });
    }

    public void addQuestion() {
        currentQuestion.setQuiz(currentQuiz);
        questionDAO.save(currentQuestion);
        // save choices
        for (Choice choice : currentChoices) {
            choice.setQuestion(currentQuestion);
            choiceDAO.save(choice);
        }
        currentQuestion = new Question();
        currentChoices = new ArrayList<>();
        questions = questionDAO.findByQuiz(currentQuiz);
    }

    public void prepareEdit(Question question) {
        this.currentQuestion = question;
        this.currentChoices = choiceDAO.findByQuestion(question);
    }

    public void updateQuestion() {
        questionDAO.update(currentQuestion);
        for (Choice choice : currentChoices) {
            if (choice.getId() == null) {
                choice.setQuestion(currentQuestion);
                choiceDAO.save(choice);
            } else {
                choiceDAO.update(choice);
            }
        }
        questions = questionDAO.findByQuiz(currentQuiz);
    }

    public void deleteQuestion(Long id) {
        questionDAO.delete(id);
        questions = questionDAO.findByQuiz(currentQuiz);
    }

    public void addChoiceField() {
        currentChoices.add(new Choice());
    }

    public void removeChoiceField(int index) {
        if (index < currentChoices.size()) {
            Choice c = currentChoices.get(index);
            if (c.getId() != null) choiceDAO.delete(c.getId());
            currentChoices.remove(index);
        }
    }

    // getters and setters
    public Long getCurrentQuizId() { return currentQuizId; }
    public void setCurrentQuizId(Long currentQuizId) { loadForQuiz(currentQuizId); }
    public Quiz getCurrentQuiz() { return currentQuiz; }
    public List<Question> getQuestions() { return questions; }
    public Question getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(Question q) { this.currentQuestion = q; }
    public List<Choice> getCurrentChoices() { return currentChoices; }
    public void setCurrentChoices(List<Choice> choices) { this.currentChoices = choices; }
}