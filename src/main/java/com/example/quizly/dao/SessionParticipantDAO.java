package com.example.quizly.dao;

import com.example.quizly.models.SessionParticipant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SessionParticipantDAO implements DAO<SessionParticipant, Long> {

    @Inject
    private EntityManagerFactory emf;

    @Override
    public void save(SessionParticipant participant) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(participant);
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
    public Optional<SessionParticipant> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(SessionParticipant.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<SessionParticipant> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT sp FROM SessionParticipant sp", SessionParticipant.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(SessionParticipant participant) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(participant);
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
            SessionParticipant participant = em.find(SessionParticipant.class, id);
            if (participant != null) {
                em.remove(participant);
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

    public Optional<SessionParticipant> findBySessionJoinCodeAndUsername(String joinCode, String username) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT sp FROM SessionParticipant sp WHERE sp.session.joinCode = :joinCode AND sp.username = :username", SessionParticipant.class)
                    .setParameter("joinCode", joinCode)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    public List<SessionParticipant> findBySessionId(Long sessionId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT sp FROM SessionParticipant sp WHERE sp.session.id = :sessionId ORDER BY sp.score DESC", SessionParticipant.class)
                    .setParameter("sessionId", sessionId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
