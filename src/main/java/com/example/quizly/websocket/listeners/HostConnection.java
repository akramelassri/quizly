package com.example.quizly.websocket.listeners;

import jakarta.websocket.Session;
import java.io.IOException;

// This lives ONLY in the WebSocket universe
public class HostConnection implements EventListener {
    private Session session;
    private String name;
    private String email;

    public HostConnection(Session session, String name, String email) {
        this.session = session;
        this.name = name;
        this.email = email;
    }

    public Session getSession() {
        return session;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public void update(String jsonEvent) {
        try {
            System.out.println("the json event is :" + jsonEvent);
            if (session.isOpen()) {
                session.getBasicRemote().sendText(jsonEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}