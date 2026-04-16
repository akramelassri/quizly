package com.example.quizly.dao;

import com.example.quizly.models.Quiz;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class QuizDAO implements DAO<Quiz, Long> {

    @Inject
    private EntityManagerFactory emf;

    @Override
    public void save(Quiz newQuiz) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(newQuiz);
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
    public Optional<Quiz> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Quiz.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Quiz> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT q FROM Quiz q", Quiz.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Quiz> findByTeacherEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT q FROM Quiz q WHERE q.teacher.email = :email ORDER BY q.createdAt DESC", Quiz.class)
                     .setParameter("email", email)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Quiz updatedQuiz) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(updatedQuiz);
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
            Quiz quiz = em.find(Quiz.class, id);
            if (quiz != null) {
                em.remove(quiz);
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
}
