package com.example.quizly.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;

@Named
@RequestScoped
public class DashboardBean {

    private String welcomeMessage;

    @Inject
    private TeacherSession teacherSession;


    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        // 1. Destroy the JSF session completely
        externalContext.invalidateSession();

        // 2. Redirect back to login
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }

    public String getWelcomeMessage() {
        String name = teacherSession.getName();
        welcomeMessage = "Welcome back, " + (name != null ? name : teacherSession.getEmail()) + "!";
        return welcomeMessage;
    }
}
