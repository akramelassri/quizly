package com.example.quizly.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.util.Optional;

import com.example.quizly.dao.TeacherDAO;
import com.example.quizly.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Named
@RequestScoped
public class LoginBean {

    // The JavaScript will magically inject the Firebase token into this variable!
    private String firebaseToken;

    @Inject
    private TeacherDAO teacherDAO;

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

        try {
            // 1. Decode the token to get the real email and name from Google
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);

            // 2. Check if the Teacher exists in Postgres
            Optional<Teacher> existingTeacher = teacherDAO.findByEmail(decodedToken.getEmail());

            if (existingTeacher.isEmpty()) {
                // 3. If they are brand new, save them to Postgres!
                // Notice we DO NOT save a password. Firebase handles that.
                Teacher newTeacher = new Teacher();
                newTeacher.setEmail(decodedToken.getEmail());
                newTeacher.setName(decodedToken.getName());

                teacherDAO.save(newTeacher);
            }

            // 4. FILL THE BUCKET!
            // Now the server will remember this specific Teacher globally for this session.
            teacherSession.setEmail(decodedToken.getEmail());
            teacherSession.setName(decodedToken.getName());
            teacherSession.setFirebaseToken(firebaseToken);

            // 5. Send them to the Dashboard
            externalContext.redirect(externalContext.getRequestContextPath() + "/teacher/quizzes.xhtml");

        } catch (Exception e) {
            // Token was fake or expired
            e.printStackTrace(); // Good for debugging if Firebase fails
            externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml?error=true");
        }
    }
}