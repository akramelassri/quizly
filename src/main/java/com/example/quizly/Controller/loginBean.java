package com.example.quizly.Controller;

import java.util.Optional;

import com.example.quizly.dao.ProfDAO;
import com.example.quizly.model.Prof;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import jakarta.inject.Inject;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;

@Named
@RequestScoped
public class loginBean {

    @Inject
    private SecurityContext securityContext;
    private String email;
    private String password;
    private Boolean rememberMe;
    private String continueWithGoogle;

    public String doLogin() {
        Credential credential = new UsernamePasswordCredential(
                email, new Password(password));
        AuthenticationStatus status = securityContext
                .authenticate(
                        getHttpRequestFromFacesContext(),
                        getHttpResponseFromFacesContext(),
                        withParams().credential(credential));

        switch (status) {
            case SUCCESS:
                // Successful login! JSF will redirect users to the secured dashboard.
                return "/prof/dashboard.xhtml?faces-redirect=true";
            case SEND_FAILURE:
                // Invalid credentials: add error message and stay on page.

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new jakarta.faces.application.FacesMessage(
                                jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                                "Invalid email or password",
                                null));
                return null;
            case SEND_CONTINUE:
                // Usually means Security Context is handling a continued HTTP flow,
                // so we instruct JSF to stop rendering the current page.
                FacesContext.getCurrentInstance().responseComplete();
                return null;
            default:
                return null;
        }
    }

    public String doLogout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    private HttpServletRequest getHttpRequestFromFacesContext() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    private HttpServletResponse getHttpResponseFromFacesContext() {
        return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
    }

    private AuthenticationParameters withParams() {
        return AuthenticationParameters.withParams();
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

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getContinueWithGoogle() {
        return null;
    }

    public void setContinueWithGoogle(String continueWithGoogle) {
        this.continueWithGoogle = continueWithGoogle;
    }
}
