package com.learningportal.lesson.dto;

import com.learningportal.lesson.entity.Lesson;
import com.learningportal.lesson.enums.LessonType;
import com.learningportal.lesson.enums.VideoProvider;

import java.util.UUID;

public record LessonResponse(
        UUID id,
        String title,
        LessonType lessonType,
        VideoProvider videoProvider,
        String videoUrl,
        String contentText,
        Integer durationSeconds,
        int displayOrder,
        boolean isPreview
) {
    public static LessonResponse fromEntity(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getLessonType(),
                lesson.getVideoProvider(),
                lesson.getVideoUrl(),
                lesson.getContentText(),
                lesson.getDurationSeconds(),
                lesson.getDisplayOrder(),
                lesson.isPreview()
        );
    }
}
