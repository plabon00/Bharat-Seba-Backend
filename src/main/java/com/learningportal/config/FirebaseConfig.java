package com.learningportal.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CREDENTIALS:}")
    private String firebaseCredentials;

    @Value("${FIREBASE_PROJECT_ID:}")
    private String projectId;

    @Value("${FIREBASE_PRIVATE_KEY:}")
    private String privateKey;

    @Value("${FIREBASE_CLIENT_EMAIL:}")
    private String clientEmail;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;
                
                if (projectId != null && !projectId.trim().isEmpty() && privateKey != null && !privateKey.trim().isEmpty()) {
                    // 1. Build from individual environment variables
                    String formattedKey = privateKey.replace("\\n", "\n");
                    String json = String.format(
                        "{\"type\":\"service_account\",\"project_id\":\"%s\",\"private_key\":\"%s\",\"client_email\":\"%s\"}",
                        projectId, formattedKey, clientEmail
                    );
                    serviceAccount = new ByteArrayInputStream(json.getBytes());
                } else if (firebaseCredentials != null && !firebaseCredentials.trim().isEmpty()) {
                    // 2. Build from FIREBASE_CREDENTIALS string (JSON or Base64)
                    String creds = firebaseCredentials.trim();
                    if (!creds.startsWith("{")) {
                        creds = new String(Base64.getDecoder().decode(creds));
                    }
                    serviceAccount = new ByteArrayInputStream(creds.getBytes());
                } else {
                    // 3. Fallback to local file
                    serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
                }
                
                if (serviceAccount == null) {
                    throw new RuntimeException("Firebase credentials not configured properly.");
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
