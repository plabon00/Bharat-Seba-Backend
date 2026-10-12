package com.learningportal.course.repository;

import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByCourseCode(String courseCode);
    boolean existsByCourseCode(String courseCode);
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    Page<Course> findByTeacherId(UUID teacherId, Pageable pageable);
    java.util.List<Course> findByTeacherId(UUID teacherId);
    Page<Course> findByCategoryIdAndStatus(UUID categoryId, CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.status = :status AND (" +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.teacher.fullName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Course> searchPublishedCourses(@Param("status") CourseStatus status, @Param("query") String query, Pageable pageable);
}
