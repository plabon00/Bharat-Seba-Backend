package com.learningportal.dashboard.dto;

public record TeacherDashboardResponse(
        long totalCourses,
        long totalStudents,
        long publishedCourses
) {}
