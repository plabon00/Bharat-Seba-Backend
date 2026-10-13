package com.learningportal.auth.dto;

public record CheckEmailResponse(
        boolean exists,
        String email,
        String message
) {}
