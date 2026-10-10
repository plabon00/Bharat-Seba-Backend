package com.learningportal.category.dto;

import com.learningportal.category.entity.Category;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String thumbnailUrl,
        int displayOrder
) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getThumbnailUrl(),
                category.getDisplayOrder()
        );
    }
}
