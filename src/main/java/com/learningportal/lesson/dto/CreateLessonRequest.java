package com.learningportal.lesson.dto;

import com.learningportal.lesson.enums.LessonType;
import com.learningportal.lesson.enums.VideoProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record CreateLessonRequest(
        @NotBlank String title,
        @NotNull LessonType lessonType,
        VideoProvider videoProvider,
        @URL String videoUrl,
        String contentText,
        Integer durationSeconds,
        boolean isPreview
) {}
