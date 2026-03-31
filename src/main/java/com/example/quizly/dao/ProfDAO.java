package com.example.quizly.dao;

import com.example.quizly.model.Prof;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

@ApplicationScoped
public class ProfDAO implements DAO<Prof, Long> {

    @Inject
    private EntityManager em;

    @Override
    public void save(Prof newProf) {
        em.getTransaction().begin();
        try {
            em.persist(newProf);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Optional<Prof> findById(Long id) {
        return Optional.ofNullable(em.find(Prof.class, id));
    }

    @Override
    public List<Prof> findAll() {
        return em.createQuery("SELECT p FROM Prof p", Prof.class).getResultList();
    }

    public Optional<Prof> findByEmail(String email) {
        return em.createQuery("SELECT p FROM Prof p WHERE p.email = :email", Prof.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void update(Prof updatedProf) {
        em.getTransaction().begin();
        try {
            em.merge(updatedProf);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        em.getTransaction().begin();
        try {
            findById(id).ifPresent(p -> em.remove(p));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
