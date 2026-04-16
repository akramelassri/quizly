package com.example.quizly.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.quizly.models.SessionStatus;

public class QuizRoom {
    private Set<UserSession> players = ConcurrentHashMap.newKeySet();
    private HostConnection quizHost;
    private SessionStatus roomStatus = SessionStatus.WAITING;

    public Set<UserSession> getPlayers() {
        return players;
    }

    public HostConnection getQuizhost() {
        return quizHost;
    }

    public SessionStatus getRoomStatus() {
        return roomStatus;
    }

    public void setQuizhost(HostConnection quizHost) {
        this.quizHost = quizHost;
    }

    public void addPlayer(UserSession player) {
        players.add(player);
    }

}
