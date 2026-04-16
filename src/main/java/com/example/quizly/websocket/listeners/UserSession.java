package com.example.quizly.websocket.listeners;

import java.io.IOException;

import com.example.quizly.websocket.EventListener;
import jakarta.websocket.Session;

public class UserSession implements EventListener {
    Session session;
    String username;
    Long participantId;
    Long points = 200L;

    public UserSession() {

    }

    public UserSession(Session session, String username, Long participantId) {
        this.session = session;
        this.username = username;
        this.participantId = participantId;
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

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public void updateScore(Long wager, boolean isCorrect) {
        if (isCorrect) {
            this.points += wager;
        } else {
            this.points -= wager;
        }
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
