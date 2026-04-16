package com.example.quizly.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Named
@RequestScoped
public class LoginBean {

    // The JavaScript will magically inject the Firebase token into this variable!
    private String firebaseToken;

    @Inject
    private TeacherSession teacherSession;

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public void completeLogin() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        // 1. (Optional) You can use the Firebase Admin SDK here to double-verify the token
        // if you want to be extra secure, just like we did in the REST filter.

        try {
            // 2. Decode the token to get the real email from Google
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            
            // 3. FILL THE BUCKET! 
            // Now the server will remember this specific Teacheressor globally.
            teacherSession.setEmail(decodedToken.getEmail());
            teacherSession.setName(decodedToken.getName());
            teacherSession.setFirebaseToken(firebaseToken);

            // 4. Send them to the Dashboard
            externalContext.redirect(externalContext.getRequestContextPath() + "/Teacher/dashboard.xhtml");

        } catch (Exception e) {
            // Token was fake or expired
            externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml?error=true");
        }
    }
}
