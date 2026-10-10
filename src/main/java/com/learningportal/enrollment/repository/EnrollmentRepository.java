package com.learningportal.enrollment.repository;

import com.learningportal.enrollment.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);
    Page<Enrollment> findByUserId(UUID userId, Pageable pageable);
}
