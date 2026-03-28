package com.example.quizly.util;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        // "quizlyPU" must match the name in your persistence.xml
        emf = Persistence.createEntityManagerFactory("quizlyPU");
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
