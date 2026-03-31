package com.example.quizly.Controller;

import com.example.quizly.dao.ProfDAO;
import com.example.quizly.model.Prof;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;

import java.io.Serializable;
import java.util.Optional;

@Named
@RequestScoped
public class DashboardBean implements Serializable {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private ProfDAO profDAO;

    private Prof currentProf;

    @PostConstruct
    public void init() {
        // Retrieves the logged-in principal dynamically and fetches database records
        if (securityContext.getCallerPrincipal() != null) {
            String email = securityContext.getCallerPrincipal().getName();
            Optional<Prof> profOpt = profDAO.findByEmail(email);
            profOpt.ifPresent(prof -> this.currentProf = prof);
        }
    }

    public Prof getCurrentProf() {
        return currentProf;
    }
}
