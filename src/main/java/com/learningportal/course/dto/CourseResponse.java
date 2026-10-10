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
        String categoryName,
        String categorySlug,
        CourseDifficulty difficulty,
        CourseStatus status,
        java.time.ZonedDateTime publishedAt
) {
    public static CourseResponse fromEntity(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnailUrl(),
                course.getTeacher().getFullName(),
                course.getCategory().getName(),
                course.getCategory().getSlug(),
                course.getDifficulty(),
                course.getStatus(),
                course.getPublishedAt()
        );
    }
}
