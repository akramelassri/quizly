package com.example.quizly.dao;

import com.example.quizly.models.Question;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class QuestionDAO implements DAO<Question, Long> {

    @Inject
    private EntityManagerFactory emf;

    @Override
    public void save(Question newQuestion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(newQuestion);
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
    public Optional<Question> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Question.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Question> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT q FROM Question q", Question.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Question updatedQuestion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(updatedQuestion);
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
            Question question = em.find(Question.class, id);
            if (question != null) {
                em.remove(question);
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
