package com.learningportal.course.controller;

import com.learningportal.course.dto.CourseResponse;
import com.learningportal.course.service.PublicCourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PublicCourseController {

    private final PublicCourseService publicCourseService;

    public PublicCourseController(PublicCourseService publicCourseService) {
        this.publicCourseService = publicCourseService;
    }

    @GetMapping("/courses")
    public ResponseEntity<Page<CourseResponse>> getCourses(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(publicCourseService.getPublishedCourses(pageable));
    }

    @GetMapping("/courses/{courseCode}")
    public ResponseEntity<CourseResponse> getCourseByCode(@PathVariable String courseCode) {
        return ResponseEntity.ok(publicCourseService.getPublishedCourseByCode(courseCode));
    }

    @GetMapping("/courses/search")
    public ResponseEntity<Page<CourseResponse>> searchCourses(
            @RequestParam("q") String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(publicCourseService.searchPublishedCourses(query, pageable));
    }

    @GetMapping("/courses/featured")
    public ResponseEntity<Page<CourseResponse>> getFeaturedCourses(@PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(publicCourseService.getPublishedCourses(pageable));
    }

    @GetMapping("/courses/popular")
    public ResponseEntity<Page<CourseResponse>> getPopularCourses(@PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(publicCourseService.getPublishedCourses(pageable));
    }

    @GetMapping("/categories/{slug}/courses")
    public ResponseEntity<Page<CourseResponse>> getCoursesByCategory(
            @PathVariable String slug,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(publicCourseService.getPublishedCoursesByCategorySlug(slug, pageable));
    }
}
