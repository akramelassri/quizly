package com.example.quizly.websocket.command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.quizly.models.Question;
import com.example.quizly.models.SessionStatus;
import com.example.quizly.websocket.QuizRoom;
import com.example.quizly.websocket.payload.ChoiceDTO;
import com.example.quizly.websocket.payload.IncomingMessage;
import com.example.quizly.websocket.payload.QuestionDTO;
import com.example.quizly.service.QuizGameService;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

public class StartGameCommand implements GameCommand {

    @Override
    public void execute(Session session, QuizRoom room, IncomingMessage message, QuizGameService gameService) {
        if (!session.equals(room.getQuizhost().getSession())) {
            System.out.println("inauthorized command from " + session.getId());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "You are not the host"));
            } catch (IOException ignored) {
            }
        }

        String pin = (String) session.getUserProperties().get("roomPin");
        gameService.updateRoomStatus(pin, SessionStatus.ACTIVE);

        room.setRoomStatus(SessionStatus.ACTIVE);

        Optional<List<Question>> questionsOpt = gameService.getQuestionsByPin(pin);

        if (questionsOpt.isPresent() && !questionsOpt.get().isEmpty()) {
            List<Question> dbQuestions = questionsOpt.get();

            // 4. THE FIX: Correctly map entities to DTOs and collect them into a new list!
            List<QuestionDTO> safeQuestions = dbQuestions.stream().map(question -> {
                // Map the choices for this question
                List<ChoiceDTO> safeChoices = question.getChoices().stream()
                        .map(choice -> new ChoiceDTO(choice.getId(), choice.getChoiceText()))
                        .collect(Collectors.toList()); // Terminal operation!

                // Return a brand new DTO
                return new QuestionDTO(question.getId(), question.getQuestionText(), safeChoices);
            }).collect(Collectors.toList()); // Terminal operation!

            // 5. Broadcast the first question to start the game
            room.setQuestions(safeQuestions);
            room.broadcastQuestion(safeQuestions.get(0));

        } else {
            System.err.println("Could not start game: No questions found!");
        }
    }
}