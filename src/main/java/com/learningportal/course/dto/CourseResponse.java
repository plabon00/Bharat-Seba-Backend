package com.learningportal.course.dto;

import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseDifficulty;
import com.learningportal.course.enums.CourseStatus;

import java.util.UUID;

public record CourseResponse(
        UUID id,
        String courseCode,
        String title,
        String description,
        String thumbnailUrl,
        String teacherName,
        String teacherEmail,
        String categoryName,
        String categorySlug,
        CourseDifficulty difficulty,
        CourseStatus status,
        String rejectionReason,
        java.time.ZonedDateTime submittedAt,
        java.time.ZonedDateTime publishedAt
) {
    public static CourseResponse fromEntity(Course course) {
        String teacherName = course.getTeacher() != null ? course.getTeacher().getFullName() : "Instructor";
        String teacherEmail = course.getTeacher() != null ? course.getTeacher().getEmail() : null;
        String categoryName = course.getCategory() != null ? course.getCategory().getName() : "General";
        String categorySlug = course.getCategory() != null ? course.getCategory().getSlug() : "general";
        return new CourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnailUrl(),
                teacherName,
                teacherEmail,
                categoryName,
                categorySlug,
                course.getDifficulty(),
                course.getStatus(),
                course.getRejectionReason(),
                course.getSubmittedAt(),
                course.getPublishedAt()
        );
    }
}
