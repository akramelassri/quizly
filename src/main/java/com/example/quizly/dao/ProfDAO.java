package com.example.quizly.dao;


import com.example.quizly.models.Prof;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

@RequestScoped
public class ProfDAO implements DAO<Prof, Long> {
    @Inject
    private EntityManagerFactory emf;
    @Override
    public void save(Prof newProf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(newProf);
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
    public Optional<Prof> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Prof.class, id));
        } catch (Exception e) {
            throw e;
        } finally {
            em.close(); 
        }
    }

    @Override
    public List<Prof> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Prof p", Prof.class).getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close(); 
        }
    }

    public Optional<Prof> findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Prof p WHERE p.email = :email", Prof.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close(); 
        }
    }

    @Override
    public void update(Prof updatedProf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(updatedProf);
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
            Prof p = em.find(Prof.class, id);
            if (p != null) {
                em.remove(p);
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