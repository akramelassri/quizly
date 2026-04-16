package com.example.quizly.controller;

import com.example.quizly.models.Quiz;
import com.example.quizly.models.Session;
import com.example.quizly.models.SessionStatus;
import com.example.quizly.dao.QuizDAO;
import com.example.quizly.dao.SessionDAO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private String welcomeMessage;
    private List<Quiz> profQuizzes;
    private String generatedJoinCode;

    @Inject
    private ProfSession profSession;

    @Inject
    private QuizDAO quizDAO;

    @Inject
    private SessionDAO sessionDAO;

    @PostConstruct
    public void init() {
        if (profSession != null && profSession.getEmail() != null) {
            profQuizzes = quizDAO.findByTeacherEmail(profSession.getEmail());
        }
    }

    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        // 1. Destroy the JSF session completely
        externalContext.invalidateSession();

        // 2. Redirect back to login
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }

    public String getWelcomeMessage() {
        String name = profSession.getName();
        welcomeMessage = "Welcome back, " + (name != null ? name : profSession.getEmail()) + "!";
        return welcomeMessage;
    }

    public String makeLive(Quiz quiz) {
        // Generate a simple 6-character random code
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        Session session = new Session();
        session.setQuiz(quiz);
        session.setJoinCode(code);
        session.setStatus(SessionStatus.WAITING);
        session.setStartedAt(new Timestamp(System.currentTimeMillis()));
        
        sessionDAO.save(session);
        
        return "/prof/host-lobby?faces-redirect=true&pin=" + code;
    }

    public List<Quiz> getProfQuizzes() {
        return profQuizzes;
    }

    public String getGeneratedJoinCode() {
        return generatedJoinCode;
    }

    public void setGeneratedJoinCode(String generatedJoinCode) {
        this.generatedJoinCode = generatedJoinCode;
    }
}
