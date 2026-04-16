package com.example.quizly.websocket;

import jakarta.websocket.Session;
import com.example.quizly.websocket.QuizRoom;


public interface GameCommand {
    void execute(Session session, QuizRoom room, IncomingMessage message);
}
