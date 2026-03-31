package com.example.quizly.Controller;

import com.example.quizly.dao.ProfDAO;
import com.example.quizly.model.Prof;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@Named
@RequestScoped
public class SignupBean {

    @Inject
    private ProfDAO profDAO;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    private String name;
    private String email;
    private String password;

    public String doSignup() {
        if (profDAO.findByEmail(email).isPresent()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "An account with this email already exists.", null));
            return null;
        }

        Prof newProf = new Prof(name, email, passwordHash.generate(password.toCharArray()));

        try {
            profDAO.save(newProf);
            // Tell JSF to carry standard messages over through the flash redirect!
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Account created successfully! You can now sign in.",
                            null));

            return "/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating account: " + e.getMessage(), null));
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
