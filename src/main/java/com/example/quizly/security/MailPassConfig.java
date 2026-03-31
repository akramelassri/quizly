package com.example.quizly.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.security.DeclareRoles;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

@CustomFormAuthenticationMechanismDefinition(loginToContinue = @LoginToContinue(loginPage = "/login.xhtml", errorPage = "/login.xhtml?error=true", useForwardToLogin = false))
@ApplicationScoped
@DeclareRoles({ "PROFESSOR" })
public class MailPassConfig {

}
