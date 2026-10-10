package com.learningportal.dashboard.dto;

public record AdminDashboardResponse(
        long totalUsers,
        long totalCourses,
        long pendingCourses
) {}
