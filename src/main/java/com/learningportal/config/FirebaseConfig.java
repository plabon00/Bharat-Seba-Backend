package com.learningportal.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CREDENTIALS:}")
    private String firebaseCredentials;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;
                
                if (firebaseCredentials != null && !firebaseCredentials.trim().isEmpty()) {
                    serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes());
                } else {
                    serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
                }
                
                if (serviceAccount == null) {
                    throw new RuntimeException("firebase-service-account.json not found in classpath and FIREBASE_CREDENTIALS is not set");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize Firebase Admin SDK: " + e.getMessage());
        }
    }
}
