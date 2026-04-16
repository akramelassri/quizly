package com.example.quizly.websocket;

import jakarta.websocket.Session;
import com.example.quizly.websocket.QuizRoom;
import com.example.quizly.service.QuizGameService;

public interface GameCommand {
    void execute(Session session, QuizRoom room, IncomingMessage message, QuizGameService gameService);
}
