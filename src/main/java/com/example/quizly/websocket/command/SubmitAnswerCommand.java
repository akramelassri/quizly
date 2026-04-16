package com.example.quizly.websocket.command;

import jakarta.websocket.Session;
import com.example.quizly.websocket.QuizRoom;
import com.example.quizly.websocket.IncomingMessage;
import com.example.quizly.service.QuizGameService;
import com.example.quizly.websocket.GameCommand;

public class SubmitAnswerCommand implements GameCommand {

    @Override
    public void execute(Session session, QuizRoom room, IncomingMessage payload, QuizGameService gameService) {

    }

}
