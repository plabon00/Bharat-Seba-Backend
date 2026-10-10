package com.learningportal.course.controller;

import com.learningportal.course.dto.CourseResponse;
import com.learningportal.course.dto.CreateCourseRequest;
import com.learningportal.course.dto.UpdateCourseRequest;
import com.learningportal.course.service.TeacherCourseService;
import com.learningportal.lesson.dto.CreateLessonRequest;
import com.learningportal.lesson.dto.LessonResponse;
import com.learningportal.lesson.dto.UpdateLessonRequest;
import com.learningportal.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher/courses")

public class TeacherCourseController {

    private final TeacherCourseService teacherCourseService;

    public TeacherCourseController(TeacherCourseService teacherCourseService) {
        this.teacherCourseService = teacherCourseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(teacherCourseService.createCourse(userDetails.getId(), request));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getMyCourses(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(teacherCourseService.getMyCourses(userDetails.getId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(teacherCourseService.getCourse(userDetails.getId(), id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(teacherCourseService.updateCourse(userDetails.getId(), id, request));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        teacherCourseService.submitCourse(userDetails.getId(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdrawCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        teacherCourseService.withdrawCourse(userDetails.getId(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveCourse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        teacherCourseService.archiveCourse(userDetails.getId(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/lessons")
    public ResponseEntity<LessonResponse> addLesson(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody CreateLessonRequest request) {
        return ResponseEntity.ok(teacherCourseService.addLesson(userDetails.getId(), id, request));
    }

    @PatchMapping("/{id}/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @PathVariable UUID lessonId,
            @Valid @RequestBody UpdateLessonRequest request) {
        return ResponseEntity.ok(teacherCourseService.updateLesson(userDetails.getId(), id, lessonId, request));
    }

    @DeleteMapping("/{id}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @PathVariable UUID lessonId) {
        teacherCourseService.deleteLesson(userDetails.getId(), id, lessonId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {
        return ResponseEntity.ok(teacherCourseService.getLessons(userDetails.getId(), id));
    }
}
