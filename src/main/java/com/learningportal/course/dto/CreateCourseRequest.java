package com.learningportal.course.dto;

import com.learningportal.course.enums.CourseDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record CreateCourseRequest(
        @NotBlank String title,
        @NotBlank String description,
        @URL String thumbnailUrl,
        @NotNull UUID categoryId,
        @NotNull CourseDifficulty difficulty
) {}
