package com.learningportal.auth.dto;

import java.util.UUID;

public record JwtResponse(
        String token,
        String type,
        UUID id,
        String email,
        String fullName,
        String role
) {
    public JwtResponse(String token, UUID id, String email, String fullName, String role) {
        this(token, "Bearer", id, email, fullName, role);
    }
}
