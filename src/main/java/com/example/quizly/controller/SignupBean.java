package com.example.quizly.controller;

import com.example.quizly.dao.TeacherDAO;
import com.example.quizly.models.Teacher;
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
    private String TeacherName;

    @Inject
    private TeacherDAO teacherDAO;
    @Inject
    private TeacherSession teacherSession;


    public void completeSignup() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        try {
            // 1. Verify the token with the Firebase Admin SDK to get their real email
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();

            // 2. Check if this Teacheressor already exists in your Postgres DB
            Optional<Teacher> existingTeacher = teacherDAO.findByEmail(email);

            if (existingTeacher.isEmpty()) {
                // 3. If they are brand new, save them to Postgres!
                // Notice we DO NOT save a password. Firebase handles that.
                Teacher newTeacher = new Teacher();
                newTeacher.setEmail(email);
                newTeacher.setName(TeacherName); 
                // newTeacher.setRole("TeacherESSOR"); // Set any default roles here
                
                teacherDAO.save(newTeacher);
            }

            teacherSession.setEmail(email);
            teacherSession.setName(TeacherName);
            teacherSession.setFirebaseToken(firebaseToken);

            // 5. Send them to the Dashboard
            externalContext.redirect(externalContext.getRequestContextPath() + "/teacher/dashboard.xhtml");

        } catch (Exception e) {
            e.printStackTrace();
            // If the token is fake or expired, kick them back to signup
            externalContext.redirect(externalContext.getRequestContextPath() + "/signup.xhtml?error=true");
        }
    }

    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }
    
    public String getTeacherName() { return TeacherName; }
    public void setTeacherName(String TeacherName) { this.TeacherName = TeacherName; }
}
