package com.example.quizly.websocket.command;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.io.IOException;

import com.example.quizly.websocket.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.example.quizly.models.SessionStatus;

public class JoinGameCommand implements GameCommand {

    @Override
    public void execute(Session session, QuizRoom room, IncomingMessage message) {

        // Ensure the room is still in the lobby state
        if (room.getRoomStatus() != SessionStatus.WAITING) {
            System.out.println("Rejected: Quiz already started.");
            return;
        }

        if ("HOST".equals(message.role)) {
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(message.firebaseToken);
                HostConnection host = new HostConnection(session, decoded.getName(), decoded.getEmail());
                room.setQuizhost(host);
                System.out.println("Host " + message.name + " registered.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Security Alert: Invalid token for host: " + message.name);
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid Token"));
                } catch (IOException ignored) {
                }
            }

        } else if ("STUDENT".equals(message.role)) {
            System.err.println("the student is joining:" + message.name);
            // Register the student
            UserSession student = new UserSession(session, message.name);
            room.addPlayer(student);
            System.out.println("Student " + message.name + " registered.");

            // Optional: Tell the host's screen to update the player count
            if (room.getQuizhost() != null) {
                room.getQuizhost().update("{\"action\":\"PLAYER_JOINED\", \"name\":\"" + message.name + "\"}");
            }
        }
    }
}