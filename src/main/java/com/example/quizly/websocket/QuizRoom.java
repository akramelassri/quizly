package com.example.quizly.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void nextQuestion() {
        currentQuestionIndex++;
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

    public void broadcastGameOver() {
        List<Map<String, Object>> scoreList = new ArrayList<>();
        for (UserSession player : players) {
            Map<String, Object> playerScore = new HashMap<>();
            playerScore.put("name", player.getUsername());
            playerScore.put("points", player.getPoints());
            scoreList.add(playerScore);
        }

        // Sort descending by points (highest score first)
        scoreList.sort((m1, m2) -> ((Long) m2.get("points")).compareTo((Long) m1.get("points")));

        // Host gets everyone
        Map<String, Object> hostPayloadMap = new HashMap<>();
        hostPayloadMap.put("action", "GAME_OVER");
        hostPayloadMap.put("scores", scoreList);
        String hostJsonPayload = jsonb.toJson(hostPayloadMap);

        try {
            if (quizHost != null) {
                quizHost.update(hostJsonPayload);
            }

            // Each student gets only their own rank and score
            for (UserSession player : players) {
                Map<String, Object> studentPayloadMap = new HashMap<>();
                studentPayloadMap.put("action", "GAME_OVER");

                // Find their rank and score
                int rank = 1;
                long points = 0;
                for (int i = 0; i < scoreList.size(); i++) {
                    if (scoreList.get(i).get("name").equals(player.getUsername())) {
                        rank = i + 1;
                        points = (Long) scoreList.get(i).get("points");
                        break;
                    }
                }

                studentPayloadMap.put("rank", rank);
                studentPayloadMap.put("points", points);

                String studentJsonPayload = jsonb.toJson(studentPayloadMap);
                player.update(studentJsonPayload);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}