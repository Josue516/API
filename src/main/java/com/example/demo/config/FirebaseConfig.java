package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        InputStream serviceAccount;
        String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
        
        if (firebaseCredentials != null && !firebaseCredentials.isBlank()) {
            serviceAccount = new ByteArrayInputStream(
                    firebaseCredentials.getBytes(StandardCharsets.UTF_8));
        } else {
            serviceAccount = new ClassPathResource("firebase-service-account.json")
                    .getInputStream();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}