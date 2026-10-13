package com.learningportal.admin.dto;

import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseDifficulty;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.lesson.dto.LessonResponse;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record AdminCoursePreviewResponse(
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
        ZonedDateTime submittedAt,
        ZonedDateTime publishedAt,
        ZonedDateTime createdAt,
        List<LessonResponse> lessons
) {
    public static AdminCoursePreviewResponse fromEntity(Course course, List<LessonResponse> lessons) {
        String teacherName = course.getTeacher() != null ? course.getTeacher().getFullName() : "Instructor";
        String teacherEmail = course.getTeacher() != null ? course.getTeacher().getEmail() : null;
        String categoryName = course.getCategory() != null ? course.getCategory().getName() : "General";
        String categorySlug = course.getCategory() != null ? course.getCategory().getSlug() : "general";
        return new AdminCoursePreviewResponse(
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
                course.getPublishedAt(),
                course.getCreatedAt(),
                lessons
        );
    }
}
