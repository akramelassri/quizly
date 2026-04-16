package com.example.quizly.websocket;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.quizly.models.Quiz;
import com.example.quizly.models.SessionStatus;
import com.example.quizly.websocket.listeners.HostConnection;
import com.example.quizly.websocket.listeners.UserSession;
import com.example.quizly.websocket.payload.QuestionDTO;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class QuizRoom {
    private Set<UserSession> players = ConcurrentHashMap.newKeySet();
    private HostConnection quizHost;
    private List<QuestionDTO> questions;
    private int currentQuestionIndex = 0;
    private SessionStatus roomStatus = SessionStatus.WAITING;
    private final Jsonb jsonb = JsonbBuilder.create();

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

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

    public void addPlayer(UserSession player) {
        players.add(player);
    }

    public void setRoomStatus(SessionStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void removePlayer(UserSession player) {
        players.remove(player);
    }

    public void broadcastQuestion(QuestionDTO question) {
        String jsonPayload = "{\"action\":\"NEW_QUESTION\", \"data\":" + jsonb.toJson(question) + "}";

        try {
            quizHost.update(jsonPayload);
            for (UserSession player : players) {
                player.update(jsonPayload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
