package com.example.quizly.dao;

import com.example.quizly.models.Session;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SessionDAO implements DAO<Session, Long> {

    @Inject
    private EntityManagerFactory emf;

    @Override
    public void save(Session newSession) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(newSession);
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
    public Optional<Session> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Session.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Session> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Session s", Session.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Session updatedSession) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(updatedSession);
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
            Session session = em.find(Session.class, id);
            if (session != null) {
                em.remove(session);
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

    public Optional<Session> findByPin(String pin) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Session s WHERE s.joinCode = :pin", Session.class)
                    .setParameter("pin", pin)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }
}
