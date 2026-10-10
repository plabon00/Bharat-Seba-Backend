package com.learningportal.enrollment.controller;

import com.learningportal.enrollment.dto.EnrollmentResponse;
import com.learningportal.enrollment.dto.ProgressResponse;
import com.learningportal.enrollment.dto.UpdateProgressRequest;
import com.learningportal.enrollment.service.EnrollmentService;
import com.learningportal.lesson.dto.LessonResponse;
import com.learningportal.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/courses/{courseCode}/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String courseCode) {
        return ResponseEntity.ok(enrollmentService.enrollInCourse(userDetails.getId(), courseCode));
    }

    @GetMapping("/enrollments/me")
    public ResponseEntity<Page<EnrollmentResponse>> getMyEnrollments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(userDetails.getId(), pageable));
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Void> cancelEnrollment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        enrollmentService.cancelEnrollment(userDetails.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/enrollments/{id}/lessons")
    public ResponseEntity<List<LessonResponse>> getEnrollmentLessons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentLessons(userDetails.getId(), id));
    }

    @GetMapping("/enrollments/{id}/progress")
    public ResponseEntity<List<ProgressResponse>> getEnrollmentProgress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentProgress(userDetails.getId(), id));
    }

    @PutMapping("/enrollments/{id}/lessons/{lessonId}/progress")
    public ResponseEntity<ProgressResponse> updateProgress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @PathVariable UUID lessonId,
            @RequestBody UpdateProgressRequest request) {
        return ResponseEntity.ok(enrollmentService.updateLessonProgress(userDetails.getId(), id, lessonId, request.positionSeconds()));
    }

    @PostMapping("/enrollments/{id}/lessons/{lessonId}/complete")
    public ResponseEntity<ProgressResponse> completeLesson(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @PathVariable UUID lessonId) {
        return ResponseEntity.ok(enrollmentService.completeLesson(userDetails.getId(), id, lessonId));
    }
}
