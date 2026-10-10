package com.learningportal.admin.service;

import com.learningportal.admin.dto.*;
import com.learningportal.category.dto.CategoryResponse;
import com.learningportal.category.entity.Category;
import com.learningportal.category.repository.CategoryRepository;
import com.learningportal.common.exception.BadRequestException;
import com.learningportal.common.exception.ResourceNotFoundException;
import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import com.learningportal.user.entity.User;
import com.learningportal.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional
public class AdminService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AdminService(CourseRepository courseRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public void approveCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (course.getStatus() != CourseStatus.PENDING_REVIEW) {
            throw new BadRequestException("Only PENDING_REVIEW courses can be approved.");
        }
        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(ZonedDateTime.now());
        course.setRejectionReason(null);
        courseRepository.save(course);
    }

    public void rejectCourse(UUID courseId, RejectCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (course.getStatus() != CourseStatus.PENDING_REVIEW) {
            throw new BadRequestException("Only PENDING_REVIEW courses can be rejected.");
        }
        course.setStatus(CourseStatus.REJECTED);
        course.setRejectionReason(request.reason());
        courseRepository.save(course);
    }

    public void unpublishCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Only PUBLISHED courses can be unpublished.");
        }
        course.setStatus(CourseStatus.DRAFT);
        courseRepository.save(course);
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name()) || categoryRepository.existsBySlug(request.slug())) {
            throw new BadRequestException("Category with name or slug already exists");
        }
        Category category = new Category();
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        category.setThumbnailUrl(request.thumbnailUrl());
        category.setDisplayOrder(request.displayOrder());
        category.setActive(request.isActive());
        
        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (request.name() != null) category.setName(request.name());
        if (request.slug() != null) category.setSlug(request.slug());
        if (request.description() != null) category.setDescription(request.description());
        if (request.thumbnailUrl() != null) category.setThumbnailUrl(request.thumbnailUrl());
        if (request.displayOrder() != null) category.setDisplayOrder(request.displayOrder());
        if (request.isActive() != null) category.setActive(request.isActive());

        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    public void updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setAccountStatus(request.status());
        userRepository.save(user);
    }

    public void updateUserRole(UUID userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(request.role());
        userRepository.save(user);
    }
}
