package com.learningportal.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UpdateProfileRequest(
        @NotBlank String fullName,
        @URL String profileImageUrl
) {}
