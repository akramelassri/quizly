package com.example.quizly.dao;


import com.example.quizly.models.Teacher;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

@RequestScoped
public class TeacherDAO implements DAO<Teacher, Long> {
    @Inject
    private EntityManagerFactory emf;
    @Override
    public void save(Teacher newTeacher) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(newTeacher);
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
    public Optional<Teacher> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Teacher.class, id));
        } catch (Exception e) {
            throw e;
        } finally {
            em.close(); 
        }
    }

    @Override
    public List<Teacher> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Teacher p", Teacher.class).getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close(); 
        }
    }

    public Optional<Teacher> findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Teacher p WHERE p.email = :email", Teacher.class)
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
    public void update(Teacher updatedTeacher) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(updatedTeacher);
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
            Teacher p = em.find(Teacher.class, id);
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