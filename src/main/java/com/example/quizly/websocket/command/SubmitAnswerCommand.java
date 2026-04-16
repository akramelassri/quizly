package com.example.quizly.websocket.command;

import com.example.quizly.websocket.QuizRoom;
import com.example.quizly.websocket.listeners.UserSession;
import com.example.quizly.websocket.payload.AnswerPayload;
import com.example.quizly.websocket.payload.IncomingMessage;
import com.example.quizly.websocket.payload.QuestionDTO;
import com.example.quizly.service.QuizGameService;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;

public class SubmitAnswerCommand implements GameCommand {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public void execute(Session session, QuizRoom room, IncomingMessage message, QuizGameService gameService) {

        // 1. Find the student who sent this message based on their WebSocket session
        UserSession student = room.getPlayers().stream()
                .filter(p -> p.getSession().equals(session))
                .findFirst()
                .orElse(null);

        if (student == null) {
            System.err.println("Unregistered student tried to submit an answer!");
            return;
        }

        try {
            // 2. Parse the nested payload string into our new Object
            AnswerPayload answerData = jsonb.fromJson(message.payload, AnswerPayload.class);

            // 3. Figure out the ID of the current active question
            int currentIndex = room.getCurrentQuestionIndex();
            QuestionDTO currentQuestion = room.getQuestions().get(currentIndex);

            // 4. Send the data to the Database!
            boolean isCorrect = gameService.processAnswer(
                    student.getParticipantId(),
                    currentQuestion.id,
                    answerData.choiceId,
                    answerData.wager);

            // 5. Update the student's score in fast RAM so we don't have to query the DB
            // later
            Long wagerLong = Long.valueOf(answerData.wager);
            student.updateScore(wagerLong, isCorrect);

            System.out.println(student.getUsername() + " submitted! Correct: " + isCorrect + " | New Score: "
                    + student.getPoints());

            // 6. Tell the host UI to update the progress bars
            if (room.getQuizhost() != null) {
                String statsUpdate = "{\"action\":\"STATS_UPDATE\", \"choiceId\":" + answerData.choiceId
                        + ", \"wager\":" + answerData.wager + "}";
                room.getQuizhost().update(statsUpdate);
            }

        } catch (Exception e) {
            System.err.println("Failed to process answer submission from: " + student.getUsername());
            e.printStackTrace();
        }
    }
}