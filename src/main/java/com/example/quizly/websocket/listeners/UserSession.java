package com.example.quizly.websocket.listeners;

import java.io.IOException;

import com.example.quizly.websocket.EventListener;
import jakarta.websocket.Session;

public class UserSession implements EventListener {
    Session session;
    String username;

    public UserSession() {

    }

    public UserSession(Session session, String username) {
        this.session = session;
        this.username = username;
    }

    public Session getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void update(String jsonEvent) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(jsonEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
