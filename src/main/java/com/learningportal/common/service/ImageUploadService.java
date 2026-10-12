package com.learningportal.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
public class ImageUploadService {

    @Value("${IMGBB_API_KEY:180243595d24120bf44f95066ee4384a}")
    private String imgbbApiKey;

    private final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";

    public String uploadImageToImgbb(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file cannot be empty");
        }

        try {
            // Convert the uploaded image file into a Base64 string for ImgBB
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("key", imgbbApiKey);
            body.add("image", base64Image);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // Send POST request to ImgBB API
            ResponseEntity<String> response = restTemplate.postForEntity(IMGBB_UPLOAD_URL, requestEntity, String.class);

            // Parse the JSON response to extract the live image URL
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String imageUrl = root.path("data").path("url").asText();

            if (imageUrl == null || imageUrl.isBlank()) {
                throw new RuntimeException("ImgBB did not return a valid image URL");
            }

            return imageUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to ImgBB: " + e.getMessage());
        }
    }
}
