package com.example.quizly.controller;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class ProfSession implements Serializable {

    private String email;
    private String name;
    private String firebaseToken;

    // The Filter uses this to check if they are allowed in
    public boolean isLoggedIn() {
        return email != null && !email.isEmpty();
    }

    // --- Getters and Setters ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }
    
    public void clear() {
        this.email = null;
        this.name = null;
        this.firebaseToken = null;
    }
}