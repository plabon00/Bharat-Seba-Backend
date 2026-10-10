package com.learningportal.admin.dto;

public record UpdateCategoryRequest(
        String name,
        String slug,
        String description,
        String thumbnailUrl,
        Integer displayOrder,
        Boolean isActive
) {}
