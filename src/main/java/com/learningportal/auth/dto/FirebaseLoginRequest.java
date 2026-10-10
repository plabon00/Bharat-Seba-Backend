package com.learningportal.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record FirebaseLoginRequest(
    @NotBlank String idToken,
    String displayName,
    String photoUrl
) {}
