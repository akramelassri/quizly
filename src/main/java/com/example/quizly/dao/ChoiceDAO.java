package com.example.quizly.dao;

import com.example.quizly.models.Choice;
import com.example.quizly.models.Question;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class ChoiceDAO implements DAO<Choice, Long> {

    @Inject
    private EntityManagerFactory emf;

    @Override
    public void save(Choice choice) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(choice);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Choice> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Choice.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Choice> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Choice c", Choice.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Choice> findByQuestion(Question question) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Choice c WHERE c.question = :question", 
                Choice.class)
                    .setParameter("question", question)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Choice choice) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(choice);
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
            Choice c = em.find(Choice.class, id);
            if (c != null) em.remove(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}