package com.example.quizly.controller;

import com.example.quizly.dao.QuizDAO;
import com.example.quizly.dao.TeacherDAO;
import com.example.quizly.models.Quiz;
import com.example.quizly.models.Teacher;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Named
@SessionScoped
public class QuizBean implements Serializable {

    @Inject private QuizDAO quizDAO;
    @Inject private TeacherDAO teacherDAO;
    @Inject private TeacherSession teacherSession;

    private List<Quiz> quizzes;
    private Quiz currentQuiz = new Quiz();

    @PostConstruct
    public void init() { loadQuizzes(); }

    public void loadQuizzes() {
        Optional<Teacher> teacher = teacherDAO.findByEmail(teacherSession.getEmail());
        teacher.ifPresent(t -> quizzes = quizDAO.findByTeacher(t));
    }

    public String createQuiz() {
        Optional<Teacher> teacher = teacherDAO.findByEmail(teacherSession.getEmail());
        if (teacher.isPresent()) {
            currentQuiz.setTeacher(teacher.get());
            currentQuiz.setCreatedAt(Timestamp.from(Instant.now()));
            quizDAO.save(currentQuiz);
            currentQuiz = new Quiz();
            loadQuizzes();
        }
        return null;
    }

    public void prepareEdit(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    public String updateQuiz() {
        quizDAO.update(currentQuiz);
        currentQuiz = new Quiz(); // Reset form
        loadQuizzes();
        return null;
    }

    // FIX: Added cancel method
    public String cancelEdit() {
        currentQuiz = new Quiz();
        return null;
    }

    public void deleteQuiz(Long id) {
        quizDAO.delete(id);
        loadQuizzes();
    }

    public List<Quiz> getQuizzes() { return quizzes; }
    public Quiz getCurrentQuiz() { return currentQuiz; }
    public void setCurrentQuiz(Quiz currentQuiz) { this.currentQuiz = currentQuiz; }

        public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        // 1. Destroy the JSF session completely
        externalContext.invalidateSession();

        // 2. Redirect back to login
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }
}