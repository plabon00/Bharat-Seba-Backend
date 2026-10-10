package com.learningportal.lesson.dto;

import com.learningportal.lesson.enums.LessonType;
import com.learningportal.lesson.enums.VideoProvider;
import org.hibernate.validator.constraints.URL;

public record UpdateLessonRequest(
        String title,
        LessonType lessonType,
        VideoProvider videoProvider,
        @URL String videoUrl,
        String contentText,
        Integer durationSeconds,
        Boolean isPreview
) {}
