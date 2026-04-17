package com.example.quizly.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import java.io.InputStream;

@ApplicationScoped
public class FirebaseInitializer {

    // This tells CDI to run this method the moment the app starts
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            // Put your downloaded JSON file in src/main/resources
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("quizly-356ee-firebase-adminsdk-fbsvc-8c31916c1d.json");;

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase Admin Initialized Successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}