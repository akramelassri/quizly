package com.example.quizly.dao;

import com.example.quizly.models.Answer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AnswerDAO implements DAO<Answer, Long> {

    @Inject
    private EntityManagerFactory emf;

    @Override
    public void save(Answer answer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(answer);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Answer> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Answer.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Answer> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Answer a", Answer.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Answer answer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(answer);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Answer answer = em.find(Answer.class, id);
            if (answer != null) {
                em.remove(answer);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Answer> findByParticipantId(Long participantId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Answer a WHERE a.participant.id = :participantId", Answer.class)
                    .setParameter("participantId", participantId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Answer> findByParticipantIdAndQuestionId(Long participantId, Long questionId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Answer a WHERE a.participant.id = :participantId AND a.question.id = :questionId", Answer.class)
                    .setParameter("participantId", participantId)
                    .setParameter("questionId", questionId)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }
}
