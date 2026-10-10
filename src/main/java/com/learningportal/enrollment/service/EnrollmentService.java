package com.learningportal.enrollment.service;

import com.learningportal.common.exception.BadRequestException;
import com.learningportal.common.exception.ResourceNotFoundException;
import com.learningportal.common.exception.UnauthorizedException;
import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import com.learningportal.enrollment.dto.EnrollmentResponse;
import com.learningportal.enrollment.dto.ProgressResponse;
import com.learningportal.enrollment.entity.Enrollment;
import com.learningportal.enrollment.enums.EnrollmentStatus;
import com.learningportal.enrollment.enums.ProgressStatus;
import com.learningportal.enrollment.repository.EnrollmentRepository;
import com.learningportal.lesson.dto.LessonResponse;
import com.learningportal.lesson.entity.Lesson;
import com.learningportal.lesson.repository.LessonRepository;
import com.learningportal.progress.entity.LessonProgress;
import com.learningportal.progress.repository.LessonProgressRepository;
import com.learningportal.user.entity.User;
import com.learningportal.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, UserRepository userRepository, LessonRepository lessonRepository, LessonProgressRepository lessonProgressRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    public EnrollmentResponse enrollInCourse(UUID userId, String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Cannot enroll in an unpublished course");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, course.getId())) {
            throw new BadRequestException("Already enrolled in this course");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        enrollment = enrollmentRepository.save(enrollment);
        return EnrollmentResponse.fromEntity(enrollment);
    }

    public Page<EnrollmentResponse> getMyEnrollments(UUID userId, Pageable pageable) {
        return enrollmentRepository.findByUserId(userId, pageable)
                .map(EnrollmentResponse::fromEntity);
    }

    public void cancelEnrollment(UUID userId, UUID enrollmentId) {
        Enrollment enrollment = getEnrollment(userId, enrollmentId);
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    public List<LessonResponse> getEnrollmentLessons(UUID userId, UUID enrollmentId) {
        Enrollment enrollment = getEnrollment(userId, enrollmentId);
        return lessonRepository.findByCourseIdOrderByDisplayOrderAsc(enrollment.getCourse().getId())
                .stream().map(LessonResponse::fromEntity).collect(Collectors.toList());
    }

    public List<ProgressResponse> getEnrollmentProgress(UUID userId, UUID enrollmentId) {
        getEnrollment(userId, enrollmentId);
        return lessonProgressRepository.findByEnrollmentId(enrollmentId)
                .stream().map(ProgressResponse::fromEntity).collect(Collectors.toList());
    }

    public ProgressResponse updateLessonProgress(UUID userId, UUID enrollmentId, UUID lessonId, int positionSeconds) {
        Enrollment enrollment = getEnrollment(userId, enrollmentId);
        Lesson lesson = lessonRepository.findByIdAndCourseId(lessonId, enrollment.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found in this course"));

        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    newProgress.setEnrollment(enrollment);
                    newProgress.setLesson(lesson);
                    newProgress.setStatus(ProgressStatus.IN_PROGRESS);
                    return newProgress;
                });

        if (progress.getStatus() != ProgressStatus.COMPLETED) {
            progress.setLastPositionSeconds(positionSeconds);
            if (progress.getStatus() == ProgressStatus.NOT_STARTED) {
                progress.setStatus(ProgressStatus.IN_PROGRESS);
            }
        }
        progress = lessonProgressRepository.save(progress);
        return ProgressResponse.fromEntity(progress);
    }

    public ProgressResponse completeLesson(UUID userId, UUID enrollmentId, UUID lessonId) {
        Enrollment enrollment = getEnrollment(userId, enrollmentId);
        Lesson lesson = lessonRepository.findByIdAndCourseId(lessonId, enrollment.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found in this course"));

        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    newProgress.setEnrollment(enrollment);
                    newProgress.setLesson(lesson);
                    return newProgress;
                });

        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setCompletedAt(ZonedDateTime.now());
        progress = lessonProgressRepository.save(progress);

        checkCourseCompletion(enrollment);

        return ProgressResponse.fromEntity(progress);
    }

    private void checkCourseCompletion(Enrollment enrollment) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByDisplayOrderAsc(enrollment.getCourse().getId());
        List<LessonProgress> progresses = lessonProgressRepository.findByEnrollmentId(enrollment.getId());

        long completedCount = progresses.stream()
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .count();

        if (completedCount == lessons.size() && lessons.size() > 0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(ZonedDateTime.now());
            enrollmentRepository.save(enrollment);
        }
    }

    private Enrollment getEnrollment(UUID userId, UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this enrollment");
        }
        return enrollment;
    }
}
