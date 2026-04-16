package com.example.quizly.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.example.quizly.dao.SessionDAO;
import com.example.quizly.models.SessionStatus;
import com.example.quizly.websocket.command.*;
import com.example.quizly.service.QuizGameService;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@ServerEndpoint("/quiz/{pin}")
public class QuizLobbyEndpoint {
    private static Map<String, QuizRoom> rooms = new ConcurrentHashMap<>();
    private static Map<String, GameCommand> commandRegistry = new HashMap<>();
    private static Jsonb jsonb = JsonbBuilder.create();
    @Inject
    private QuizGameService gameService;

    static {
        commandRegistry.put("JOIN", new JoinGameCommand());
        commandRegistry.put("START_GAME", new StartGameCommand());
        commandRegistry.put("SUBMIT_ANSWER", new SubmitAnswerCommand());
    }

    @OnOpen
    public void OnOpen(Session session, @PathParam("pin") String pin) {
        try {
            boolean isRoomLive = false;

            Optional<com.example.quizly.models.Session> roomOptional = gameService.getSessionDao().findByPin(pin);

            if (roomOptional.isPresent()) {

                com.example.quizly.models.Session room = roomOptional.get();

                if (room.getStatus() != SessionStatus.WAITING) {
                    CloseReason reason = new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,
                            "the session but it started or ended");
                    session.close(reason);
                    return;
                }

                isRoomLive = true;

            }

            if (!isRoomLive) {
                // The PIN is bad. Close the connection gracefully and immediately.
                CloseReason reason = new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid PIN");
                session.close(reason);
                return;
            }
            session.getUserProperties().put("roomPin", pin);

            // 3. Make sure the QuizRoom object exists in our Map
            rooms.computeIfAbsent(pin, k -> new QuizRoom());

            System.out.println("Connection opened for pin: " + pin + ". Waiting for JOIN JSON...");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnMessage
    public void onMessage(String jsonMessage, Session session) {
        try {
            // 1. Convert the raw JSON string into our Java object
            IncomingMessage payload = jsonb.fromJson(jsonMessage, IncomingMessage.class);
            System.out.println("Received message: " + jsonMessage);
            // 2. Figure out which room this user belongs to
            String pin = (String) session.getUserProperties().get("roomPin");
            QuizRoom room = rooms.get(pin);

            if (room == null) {
                System.out.println("Room not found for message.");
                return;
            }

            // 3. Look up the correct command based on the JSON "action" property
            GameCommand command = commandRegistry.get(payload.action);

            // 4. Execute the logic!
            if (command != null) {
                command.execute(session, room, payload, gameService);
            } else {
                System.err.println("Unknown action received: " + payload.action);
            }

        } catch (Exception e) {
            System.err.println("Failed to parse incoming JSON: " + jsonMessage);
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        // Find out who disconnected
        String pin = (String) session.getUserProperties().get("roomPin");
        if (pin != null && rooms.containsKey(pin)) {
            QuizRoom room = rooms.get(pin);

            // TODO: If the host disconnects, end the game.
            // If a student disconnects, remove them from the room.players list.
        }
    }
}
