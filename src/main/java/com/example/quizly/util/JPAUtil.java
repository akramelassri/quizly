package com.example.quizly.util;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
@ApplicationScoped
public class JPAUtil {
    private EntityManagerFactory emf;

    // @PostConstruct tells WildFly: "Run this method exactly once right after you create this class."
    // We use this instead of a static block because CDI hates static!
    @PostConstruct
    public void init() {
        emf = Persistence.createEntityManagerFactory("quizlyPU");
    }

    // THE MAGIC: Tells WildFly how to provide an EntityManagerFactory to your DAOs
    @Produces
    @ApplicationScoped // Tells WildFly the produced factory should live forever
    public EntityManagerFactory produceEntityManagerFactory() {
        return emf;
    }

    // @PreDestroy tells WildFly: "Run this right before the server shuts down."
    // This is perfect for cleaning up memory!
    @PreDestroy
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
