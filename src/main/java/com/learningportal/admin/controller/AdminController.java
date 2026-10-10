package com.learningportal.admin.controller;

import com.learningportal.admin.dto.*;
import com.learningportal.admin.service.AdminService;
import com.learningportal.category.dto.CategoryResponse;
import com.learningportal.course.dto.CourseResponse;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import com.learningportal.user.dto.UserResponse;
import com.learningportal.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, CourseRepository courseRepository, UserRepository userRepository) {
        this.adminService = adminService;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/courses")
    public ResponseEntity<Page<CourseResponse>> getCourses(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseRepository.findAll(pageable).map(CourseResponse::fromEntity));
    }

    @GetMapping("/courses/pending")
    public ResponseEntity<Page<CourseResponse>> getPendingCourses(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseRepository.findByStatus(CourseStatus.PENDING_REVIEW, pageable).map(CourseResponse::fromEntity));
    }

    @PostMapping("/courses/{id}/approve")
    public ResponseEntity<Void> approveCourse(@PathVariable UUID id) {
        adminService.approveCourse(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/courses/{id}/reject")
    public ResponseEntity<Void> rejectCourse(
            @PathVariable UUID id,
            @Valid @RequestBody RejectCourseRequest request) {
        adminService.rejectCourse(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/courses/{id}/unpublish")
    public ResponseEntity<Void> unpublishCourse(@PathVariable UUID id) {
        adminService.unpublishCourse(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(adminService.createCategory(request));
    }

    @PatchMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(adminService.updateCategory(id, request));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable).map(UserResponse::fromEntity));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        adminService.updateUserStatus(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        adminService.updateUserRole(id, request);
        return ResponseEntity.ok().build();
    }
}
