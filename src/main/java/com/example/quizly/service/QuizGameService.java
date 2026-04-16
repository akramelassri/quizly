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
            Session dbSession = em.createQuery(
                    "SELECT s FROM Session s WHERE s.joinCode = :pin", Session.class)
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

    public Long addPlayerToSession(String pin, String username) {
        EntityManager em = emf.createEntityManager();
        try {
            // 1. BEGIN THE TRANSACTION
            em.getTransaction().begin();

            Session dbSession = em.createQuery(
                    "SELECT s FROM Session s WHERE s.joinCode = :pin", Session.class)
                    .setParameter("pin", pin)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (dbSession == null) {
                em.getTransaction().rollback();
                return null;
            }

            SessionParticipant participant = new SessionParticipant();
            participant.setSession(dbSession);
            participant.setUsername(username);
            participant.setScore(200);

            // 2. PERSIST THE DATA
            em.persist(participant);

            // 3. COMMIT THE TRANSACTION (This executes the INSERT statement)
            em.getTransaction().commit();

            // 4. Return the newly generated ID!
            return participant.getId();

        } catch (Exception e) {
            // If anything goes wrong, cancel the transaction so the database doesn't get
            // corrupted
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean processAnswer(Long participantId, Long questionId, Long choiceId, Integer wager) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Fetch the Participant and the Choice (We need to read if it's correct)
            SessionParticipant participant = em.find(SessionParticipant.class, participantId);
            Choice choice = em.find(Choice.class, choiceId);

            // We use getReference for Question because we don't need to read its data,
            // we just need its ID for the Foreign Key. This saves a database query!
            Question question = em.getReference(Question.class, questionId);

            // 2. Create the Answer record
            Answer answer = new Answer();
            answer.setParticipant(participant);
            answer.setQuestion(question);
            answer.setChoice(choice);
            answer.setWager(wager);
            System.out.println("Answer: " + answer);

            em.persist(answer);
            System.out.println("Answer persisted");

            // 3. Calculate the new score
            boolean isCorrect = choice.isCorrect();
            if (isCorrect) {
                participant.setScore(participant.getScore() + wager);
            } else {
                participant.setScore(participant.getScore() - wager);
            }

            em.merge(participant);

            // 4. Commit the transaction (Saves both the Answer and the new Score)
            em.getTransaction().commit();

            return isCorrect;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    // public void addParticipant(String pin, String username) { ... }
}