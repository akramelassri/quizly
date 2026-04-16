package com.example.quizly.service;

import com.example.quizly.dao.*;
import com.example.quizly.models.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class QuizGameService {

    @Inject
    private SessionDAO sessionDao;
    @Inject
    private QuizDAO quizDAO;
    @Inject
    private QuestionDAO questionDAO;
    @Inject
    private ChoiceDAO choiceDAO;

    @Inject
    private EntityManagerFactory emf;

    // @Inject
    // private ParticipantDAO participantDao;

    public SessionDAO getSessionDao() {
        return sessionDao;
    }

    // --- Create helper methods for your commands to use ---

    public void updateRoomStatus(String pin, SessionStatus newStatus) {
        Optional<com.example.quizly.models.Session> dbOpt = sessionDao.findByPin(pin);
        if (dbOpt.isPresent()) {
            com.example.quizly.models.Session dbSession = dbOpt.get();
            dbSession.setStatus(newStatus);
            sessionDao.update(dbSession);
        }
    }

    public Optional<List<Question>> getQuestionsByPin(String pin) {
        EntityManager em = emf.createEntityManager();
        try {
            // Fetch the session using a manual query so we control the EntityManager
            com.example.quizly.models.Session dbSession = em.createQuery(
                    "SELECT s FROM Session s WHERE s.joinCode = :pin", com.example.quizly.models.Session.class)
                    .setParameter("pin", pin)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (dbSession == null || dbSession.getQuiz() == null) {
                return Optional.empty();
            }

            // Navigate the tree WHILE the connection is still open
            Quiz quiz = dbSession.getQuiz();
            List<Question> questions = quiz.getQuestions();

            // CRITICAL: Force Hibernate to load the lazy collections into RAM
            // Calling .size() forces Hibernate to execute the SELECT query immediately.
            questions.size();
            for (Question q : questions) {
                q.getChoices().size();
            }

            // Now the whole tree is loaded into memory!
            return Optional.of(questions);

        } finally {
            // Safely close the connection. The data is already in RAM,
            // so StartGameCommand won't crash when mapping the DTOs!
            em.close();
        }
    }

    // public void addParticipant(String pin, String username) { ... }
}