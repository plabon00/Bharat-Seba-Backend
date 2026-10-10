package com.learningportal.course.service;

import com.learningportal.category.entity.Category;
import com.learningportal.category.repository.CategoryRepository;
import com.learningportal.common.exception.ResourceNotFoundException;
import com.learningportal.course.dto.CourseResponse;
import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PublicCourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public PublicCourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<CourseResponse> getPublishedCourses(Pageable pageable) {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable)
                .map(CourseResponse::fromEntity);
    }

    public CourseResponse getPublishedCourseByCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .filter(c -> c.getStatus() == CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found or not published: " + courseCode));
        return CourseResponse.fromEntity(course);
    }

    public Page<CourseResponse> getPublishedCoursesByCategorySlug(String slug, Pageable pageable) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug));
        return courseRepository.findByCategoryIdAndStatus(category.getId(), CourseStatus.PUBLISHED, pageable)
                .map(CourseResponse::fromEntity);
    }

    public Page<CourseResponse> searchPublishedCourses(String query, Pageable pageable) {
        return courseRepository.searchPublishedCourses(CourseStatus.PUBLISHED, query, pageable)
                .map(CourseResponse::fromEntity);
    }
}
