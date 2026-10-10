package com.learningportal.dashboard.controller;

import com.learningportal.dashboard.dto.AdminDashboardResponse;
import com.learningportal.dashboard.dto.LearnerDashboardResponse;
import com.learningportal.dashboard.dto.TeacherDashboardResponse;
import com.learningportal.dashboard.service.DashboardService;
import com.learningportal.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/me")
    public ResponseEntity<LearnerDashboardResponse> getLearnerDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getLearnerDashboard(userDetails.getId()));
    }

    @GetMapping("/teacher/dashboard")

    public ResponseEntity<TeacherDashboardResponse> getTeacherDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getTeacherDashboard(userDetails.getId()));
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }
}
