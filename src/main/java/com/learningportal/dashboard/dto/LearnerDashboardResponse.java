package com.learningportal.dashboard.dto;

public record LearnerDashboardResponse(
        long totalEnrollments,
        long completedCourses,
        long inProgressCourses
) {}
