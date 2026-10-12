package com.learningportal.course.dto;

import com.learningportal.course.enums.CourseDifficulty;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record UpdateCourseRequest(
        String title,
        String description,
        String thumbnailUrl,
        UUID categoryId,
        CourseDifficulty difficulty
) {}
