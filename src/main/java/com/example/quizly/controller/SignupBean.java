package com.example.quizly.controller;

import com.example.quizly.dao.ProfDAO;
import com.example.quizly.models.Prof;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.util.Optional;


@Named
@RequestScoped
public class SignupBean {
    private String firebaseToken;
    private String profName;

    @Inject
    private ProfDAO profDAO;
    @Inject
    private ProfSession profSession;


    public void completeSignup() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        try {
            // 1. Verify the token with the Firebase Admin SDK to get their real email
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();

            // 2. Check if this Professor already exists in your Postgres DB
            Optional<Prof> existingProf = profDAO.findByEmail(email);

            if (existingProf.isEmpty()) {
                // 3. If they are brand new, save them to Postgres!
                // Notice we DO NOT save a password. Firebase handles that.
                Prof newProf = new Prof();
                newProf.setEmail(email);
                newProf.setName(profName); 
                // newProf.setRole("PROFESSOR"); // Set any default roles here
                
                profDAO.save(newProf);
            }

            profSession.setEmail(email);
            profSession.setName(profName);
            profSession.setFirebaseToken(firebaseToken);

            // 5. Send them to the Dashboard
            externalContext.redirect(externalContext.getRequestContextPath() + "/prof/dashboard.xhtml");

        } catch (Exception e) {
            e.printStackTrace();
            // If the token is fake or expired, kick them back to signup
            externalContext.redirect(externalContext.getRequestContextPath() + "/signup.xhtml?error=true");
        }
    }

    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }
    
    public String getProfName() { return profName; }
    public void setProfName(String profName) { this.profName = profName; }
}
