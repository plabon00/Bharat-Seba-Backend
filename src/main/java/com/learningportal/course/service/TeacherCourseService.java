package com.learningportal.course.service;

import com.learningportal.category.entity.Category;
import com.learningportal.category.repository.CategoryRepository;
import com.learningportal.common.exception.BadRequestException;
import com.learningportal.common.exception.ResourceNotFoundException;
import com.learningportal.common.exception.UnauthorizedException;
import com.learningportal.course.dto.CourseResponse;
import com.learningportal.course.dto.CreateCourseRequest;
import com.learningportal.course.dto.UpdateCourseRequest;
import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import com.learningportal.lesson.dto.CreateLessonRequest;
import com.learningportal.lesson.dto.LessonResponse;
import com.learningportal.lesson.dto.UpdateLessonRequest;
import com.learningportal.lesson.entity.Lesson;
import com.learningportal.lesson.enums.LessonType;
import com.learningportal.lesson.repository.LessonRepository;
import com.learningportal.user.entity.User;
import com.learningportal.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherCourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public TeacherCourseService(CourseRepository courseRepository, CategoryRepository categoryRepository, UserRepository userRepository, LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    public Page<CourseResponse> getMyCourses(UUID teacherId, Pageable pageable) {
        return courseRepository.findByTeacherId(teacherId, pageable)
                .map(CourseResponse::fromEntity);
    }

    public CourseResponse getCourse(UUID teacherId, UUID courseId) {
        Course course = getTeacherCourse(teacherId, courseId);
        return CourseResponse.fromEntity(course);
    }

    public CourseResponse createCourse(UUID teacherId, CreateCourseRequest request) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Course course = new Course();
        course.setCourseCode(generateUniqueCourseCode());
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setThumbnailUrl(StringUtils.hasText(request.thumbnailUrl()) ? request.thumbnailUrl().trim() : null);
        course.setTeacher(teacher);
        course.setCategory(category);
        course.setDifficulty(request.difficulty());
        course.setStatus(CourseStatus.DRAFT);

        course = courseRepository.save(course);
        return CourseResponse.fromEntity(course);
    }

    public CourseResponse updateCourse(UUID teacherId, UUID courseId, UpdateCourseRequest request) {
        Course course = getTeacherCourse(teacherId, courseId);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new BadRequestException("Cannot edit a course while it is pending review.");
        }

        if (request.title() != null) course.setTitle(request.title());
        if (request.description() != null) course.setDescription(request.description());
        if (request.thumbnailUrl() != null) {
            course.setThumbnailUrl(StringUtils.hasText(request.thumbnailUrl()) ? request.thumbnailUrl().trim() : null);
        }
        if (request.difficulty() != null) course.setDifficulty(request.difficulty());
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            course.setCategory(category);
        }

        course = courseRepository.save(course);
        return CourseResponse.fromEntity(course);
    }

    public void submitCourse(UUID teacherId, UUID courseId) {
        Course course = getTeacherCourse(teacherId, courseId);
        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BadRequestException("Only DRAFT or REJECTED courses can be submitted.");
        }

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        if (lessons.isEmpty()) {
            throw new BadRequestException("Course must have at least one lesson before submission.");
        }

        course.setStatus(CourseStatus.PENDING_REVIEW);
        course.setSubmittedAt(ZonedDateTime.now());
        courseRepository.save(course);
    }

    public void withdrawCourse(UUID teacherId, UUID courseId) {
        Course course = getTeacherCourse(teacherId, courseId);
        if (course.getStatus() != CourseStatus.PENDING_REVIEW && course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Only PENDING_REVIEW or PUBLISHED courses can be withdrawn.");
        }

        course.setStatus(CourseStatus.DRAFT);
        courseRepository.save(course);
    }

    public void archiveCourse(UUID teacherId, UUID courseId) {
        Course course = getTeacherCourse(teacherId, courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Only PUBLISHED courses can be archived.");
        }

        course.setStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);
    }

    // --- LESSON MANAGEMENT ---

    public LessonResponse addLesson(UUID teacherId, UUID courseId, CreateLessonRequest request) {
        Course course = getTeacherCourse(teacherId, courseId);
        validateLessonContent(request.lessonType(), request.videoUrl(), request.contentText());

        List<Lesson> existingLessons = lessonRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        int nextOrder = existingLessons.size() + 1;

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(request.title());
        lesson.setLessonType(request.lessonType());
        lesson.setVideoProvider(request.videoProvider());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setContentText(request.contentText());
        lesson.setDurationSeconds(request.durationSeconds());
        lesson.setPreview(request.isPreview());
        lesson.setDisplayOrder(nextOrder);

        lesson = lessonRepository.save(lesson);
        return LessonResponse.fromEntity(lesson);
    }

    public LessonResponse updateLesson(UUID teacherId, UUID courseId, UUID lessonId, UpdateLessonRequest request) {
        Course course = getTeacherCourse(teacherId, courseId);
        Lesson lesson = lessonRepository.findByIdAndCourseId(lessonId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        if (request.title() != null) lesson.setTitle(request.title());
        if (request.lessonType() != null) lesson.setLessonType(request.lessonType());
        if (request.videoProvider() != null) lesson.setVideoProvider(request.videoProvider());
        if (request.videoUrl() != null) lesson.setVideoUrl(request.videoUrl());
        if (request.contentText() != null) lesson.setContentText(request.contentText());
        if (request.durationSeconds() != null) lesson.setDurationSeconds(request.durationSeconds());
        if (request.isPreview() != null) lesson.setPreview(request.isPreview());

        validateLessonContent(lesson.getLessonType(), lesson.getVideoUrl(), lesson.getContentText());

        lesson = lessonRepository.save(lesson);
        return LessonResponse.fromEntity(lesson);
    }

    public void deleteLesson(UUID teacherId, UUID courseId, UUID lessonId) {
        Course course = getTeacherCourse(teacherId, courseId);
        Lesson lesson = lessonRepository.findByIdAndCourseId(lessonId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        lessonRepository.delete(lesson);
        // Might need to reorder remaining lessons, skipped for brevity or do it via a reorder endpoint
    }

    public List<LessonResponse> getLessons(UUID teacherId, UUID courseId) {
        getTeacherCourse(teacherId, courseId);
        return lessonRepository.findByCourseIdOrderByDisplayOrderAsc(courseId)
                .stream().map(LessonResponse::fromEntity).collect(Collectors.toList());
    }

    private Course getTeacherCourse(UUID teacherId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this course");
        }
        return course;
    }

    private String generateUniqueCourseCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (courseRepository.existsByCourseCode(code));
        return code;
    }

    private void validateLessonContent(LessonType type, String videoUrl, String contentText) {
        if (type == LessonType.VIDEO && !StringUtils.hasText(videoUrl)) {
            throw new BadRequestException("Video URL is required for VIDEO lesson");
        }
        if (type == LessonType.TEXT && !StringUtils.hasText(contentText)) {
            throw new BadRequestException("Content text is required for TEXT lesson");
        }
    }
}
