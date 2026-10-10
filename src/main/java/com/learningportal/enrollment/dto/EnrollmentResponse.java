package com.learningportal.enrollment.dto;

import com.learningportal.enrollment.entity.Enrollment;
import com.learningportal.enrollment.enums.EnrollmentStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        String courseThumbnailUrl,
        EnrollmentStatus status,
        ZonedDateTime enrolledAt,
        ZonedDateTime completedAt
) {
    public static EnrollmentResponse fromEntity(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getThumbnailUrl(),
                enrollment.getStatus(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedAt()
        );
    }
}
