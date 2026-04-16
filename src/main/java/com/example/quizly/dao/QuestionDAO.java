package com.example.quizly.dao;

import com.example.quizly.models.Question;
import com.example.quizly.models.Quiz;
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
    public void save(Question question) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(question);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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

    public List<Question> findByQuiz(Quiz quiz) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
            "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.quiz = :quiz", 
            Question.class)
            .setParameter("quiz", quiz)
            .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Question question) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(question);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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
            Question q = em.find(Question.class, id);
            if (q != null) em.remove(q);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}