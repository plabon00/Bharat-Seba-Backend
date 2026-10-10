package com.learningportal.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Runs every 4 minutes (240000 milliseconds)
    @Scheduled(fixedRate = 240000)
    public void pingSelf() {
        try {
            // Ping the actuator health endpoint to keep Render awake
            String url = "https://bharat-seba-backend.onrender.com/actuator/health";
            restTemplate.getForObject(url, String.class);
            System.out.println("PING: Sent keep-alive request to Render to prevent sleep.");
        } catch (Exception e) {
            System.err.println("PING FAILED: " + e.getMessage());
        }
    }
}
