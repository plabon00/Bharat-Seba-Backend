package com.learningportal.dashboard.service;

import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import com.learningportal.dashboard.dto.AdminDashboardResponse;
import com.learningportal.dashboard.dto.LearnerDashboardResponse;
import com.learningportal.dashboard.dto.TeacherDashboardResponse;
import com.learningportal.enrollment.entity.Enrollment;
import com.learningportal.enrollment.enums.EnrollmentStatus;
import com.learningportal.enrollment.repository.EnrollmentRepository;
import com.learningportal.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public DashboardService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public LearnerDashboardResponse getLearnerDashboard(UUID userId) {
        // Fetching all enrollments for simple counting.
        // In real app, use a custom count query.
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        
        long total = enrollments.size();
        long completed = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long inProgress = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE).count();
        
        return new LearnerDashboardResponse(total, completed, inProgress);
    }

    public TeacherDashboardResponse getTeacherDashboard(UUID teacherId) {
        long totalCourses = courseRepository.findByTeacherId(teacherId, Pageable.unpaged()).getTotalElements();
        // Since we don't have a direct query for total distinct students per teacher, we'll return 0 for now.
        // In a real app, write a native query.
        return new TeacherDashboardResponse(totalCourses, 0, 0); 
    }

    public AdminDashboardResponse getAdminDashboard() {
        long users = userRepository.count();
        long courses = courseRepository.count();
        long pending = courseRepository.findByStatus(CourseStatus.PENDING_REVIEW, Pageable.unpaged()).getTotalElements();
        return new AdminDashboardResponse(users, courses, pending);
    }
}
