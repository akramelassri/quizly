package com.example.quizly.websocket.command;

import com.example.quizly.websocket.QuizRoom;

import java.io.IOException;

import com.example.quizly.models.SessionStatus;
import com.example.quizly.service.QuizGameService;
import com.example.quizly.websocket.payload.IncomingMessage;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

public class NextQuestionCommand implements GameCommand {

    @Override
    public void execute(Session session, QuizRoom room, IncomingMessage message, QuizGameService gameService) {
        if (!session.equals(room.getQuizhost().getSession())) {
            System.out.println("inauthorized command from " + session.getId());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "You are not the host"));
            } catch (IOException ignored) {
            }
        }
        room.nextQuestion();

        // 2. Check if we have run out of questions
        if (room.getCurrentQuestionIndex() < room.getQuestions().size()) {
            // Send the next question
            room.broadcastQuestion(room.getQuestions().get(room.getCurrentQuestionIndex()));
        } else {
            // We reached the end! End the game.
            room.broadcastGameOver();
            String pin = (String) session.getUserProperties().get("roomPin");
            gameService.updateRoomStatus(pin, SessionStatus.FINISHED);
            room.setRoomStatus(SessionStatus.FINISHED);

            System.out.println("game over");
        }
    }
}