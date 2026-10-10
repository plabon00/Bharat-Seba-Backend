package com.learningportal.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank String name,
        @NotBlank String slug,
        String description,
        String thumbnailUrl,
        int displayOrder,
        boolean isActive
) {}
