package com.example.quizly.controller;

import com.example.quizly.models.Choice;
import com.example.quizly.models.Question;
import com.example.quizly.models.Quiz;
import com.example.quizly.models.Teacher;
import com.example.quizly.dao.QuizDAO;
import com.example.quizly.dao.TeacherDAO;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
@ViewScoped
public class QuizBean implements Serializable {

    @Inject
    private QuizDAO quizDAO;
    @Inject
    private TeacherDAO teacherDAO;
    @Inject
    private TeacherSession teacherSession;

    private List<Quiz> quizzes;
    private Quiz currentQuiz;

    public QuizBean() {
        resetCurrentQuiz();
    }

    @PostConstruct
    public void init() {
        loadQuizzes();
    }

    private void resetCurrentQuiz() {
        currentQuiz = new Quiz();
        currentQuiz.setQuestions(new ArrayList<>());
    }

    // ==========================================
    // DASHBOARD & CRUD LOGIC (From develop branch)
    // ==========================================

    public void loadQuizzes() {
        Optional<Teacher> teacher = teacherDAO.findByEmail(teacherSession.getEmail());
        teacher.ifPresent(t -> quizzes = quizDAO.findByTeacher(t));
    }

    public String createQuiz() {
        Optional<Teacher> teacher = teacherDAO.findByEmail(teacherSession.getEmail());
        if (teacher.isPresent()) {
            currentQuiz.setTeacher(teacher.get());
            currentQuiz.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            quizDAO.save(currentQuiz);
            resetCurrentQuiz();
            loadQuizzes();
        }
        return null;
    }

    public void prepareEdit(Quiz quiz) {
        this.currentQuiz = quiz;
        if (this.currentQuiz.getQuestions() == null) {
            this.currentQuiz.setQuestions(new ArrayList<>());
        }
    }

    public String updateQuiz() {
        quizDAO.update(currentQuiz);
        resetCurrentQuiz();
        loadQuizzes();
        return null;
    }

    public String cancelEdit() {
        resetCurrentQuiz();
        return null;
    }

    public void deleteQuiz(Long id) {
        quizDAO.delete(id);
        loadQuizzes();
    }

    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.invalidateSession();
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }

    // ==========================================
    // COMPLEX BUILDER LOGIC (From your local branch)
    // ==========================================

    public void addQuestion() {
        Question q = new Question();
        q.setChoices(new ArrayList<>());
        q.setQuiz(currentQuiz);

        if (currentQuiz.getQuestions() == null) {
            currentQuiz.setQuestions(new ArrayList<>());
        }
        currentQuiz.getQuestions().add(q);
    }

    public void removeQuestion(Question q) {
        if (currentQuiz.getQuestions() != null) {
            currentQuiz.getQuestions().remove(q);
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
        currentQuiz.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        System.out.println("------------------------------------------------");
        System.out.println("prof: " + teacherSession.getEmail());

        // Find the current Prof from DB based on session email
        Teacher currentTeacher = teacherDAO.findByEmail(teacherSession.getEmail()).orElse(null);
        if (currentTeacher == null) {
            // Handle error: not logged in properly or user not found
            return "/login?faces-redirect=true";
        }
        currentQuiz.setTeacher(currentTeacher);

        // 1. Ensure all associations are correctly set before persisting so that
        // Cascade works
        if (currentQuiz.getQuestions() != null) {
            for (Question q : currentQuiz.getQuestions()) {
                q.setQuiz(currentQuiz); // Ensure bidirectional association

                if (q.getChoices() != null) {
                    for (Choice c : q.getChoices()) {
                        c.setQuestion(q); // Ensure bidirectional association
                    }
                }
            }
        }

        // 2. Save standard quiz (and its questions/choices automatically due to
        // CascadeType.ALL)
        quizDAO.save(currentQuiz);

        return "/teacher/dashboard?faces-redirect=true";
    }
    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public Quiz getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(Quiz currentQuiz) {
        this.currentQuiz = currentQuiz;
    }

    // Keeping getQuiz() and setQuiz() as aliases so your XHTML pages don't break!
    public Quiz getQuiz() {
        return currentQuiz;
    }

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }
}