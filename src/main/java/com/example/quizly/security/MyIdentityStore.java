package com.example.quizly.security;

import com.example.quizly.dao.ProfDAO;
import com.example.quizly.model.Prof;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.*;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

@ApplicationScoped
public class MyIdentityStore implements IdentityStore {

    @Inject
    private ProfDAO profDAO;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public EnumSet<ValidationType> validationTypes() {
        return EnumSet.of(ValidationType.PROVIDE_GROUPS, ValidationType.VALIDATE);
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential)) {
            return CredentialValidationResult.INVALID_RESULT;
        }
        UsernamePasswordCredential userCred = (UsernamePasswordCredential) credential;
        System.out.println("====================================");
        System.out.println(">>> MyIdentityStore.validate() called for email: " + userCred.getCaller());
        Optional<Prof> profOpt = profDAO.findByEmail(userCred.getCaller());

        System.out.println(">>> Prof found in DB? " + profOpt.isPresent());

        if (profOpt.isPresent()) {
            Prof prof = profOpt.get();
            System.out.println("Prof found: " + prof.getEmail());
            System.out.println("Prof password: " + prof.getPassword());
            System.out.println("prof hash password: "
                    + passwordHash.verify(userCred.getPassword().getValue(), prof.getPassword()));
            System.out.println("User password: " + userCred.getPassword());

            boolean matches = passwordHash.verify(userCred.getPassword().getValue(), prof.getPassword());

            if (matches) {
                return new CredentialValidationResult(prof.getEmail(), Collections.singleton("PROFESSOR"));
            }
        }

        return CredentialValidationResult.INVALID_RESULT;
    }
}
