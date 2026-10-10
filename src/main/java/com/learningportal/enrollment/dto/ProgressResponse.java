package com.learningportal.enrollment.dto;

import com.learningportal.enrollment.enums.ProgressStatus;
import com.learningportal.progress.entity.LessonProgress;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ProgressResponse(
        UUID id,
        UUID enrollmentId,
        UUID lessonId,
        ProgressStatus status,
        int lastPositionSeconds,
        ZonedDateTime startedAt,
        ZonedDateTime completedAt
) {
    public static ProgressResponse fromEntity(LessonProgress progress) {
        return new ProgressResponse(
                progress.getId(),
                progress.getEnrollment().getId(),
                progress.getLesson().getId(),
                progress.getStatus(),
                progress.getLastPositionSeconds(),
                progress.getStartedAt(),
                progress.getCompletedAt()
        );
    }
}
