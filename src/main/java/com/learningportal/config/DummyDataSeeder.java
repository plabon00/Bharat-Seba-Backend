package com.learningportal.config;

import com.learningportal.category.entity.Category;
import com.learningportal.category.repository.CategoryRepository;
import com.learningportal.course.entity.Course;
import com.learningportal.course.enums.CourseDifficulty;
import com.learningportal.course.enums.CourseStatus;
import com.learningportal.course.repository.CourseRepository;
import com.learningportal.lesson.entity.Lesson;
import com.learningportal.lesson.enums.LessonType;
import com.learningportal.lesson.repository.LessonRepository;
import com.learningportal.user.entity.User;
import com.learningportal.user.enums.UserRole;
import com.learningportal.user.enums.UserStatus;
import com.learningportal.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.ZonedDateTime;

@Component
public class DummyDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final PasswordEncoder passwordEncoder;

    public DummyDataSeeder(UserRepository userRepository,
            CategoryRepository categoryRepository,
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String passwordHash = passwordEncoder.encode("password123");

        // 1. Create 3 Admin Users safely
        createUserIfNotFound("Admin One", "admin1@bharatseva.org", passwordHash, UserRole.ADMIN);
        createUserIfNotFound("Admin Two", "admin2@bharatseva.org", passwordHash, UserRole.ADMIN);
        createUserIfNotFound("Admin Three", "admin3@bharatseva.org", passwordHash, UserRole.ADMIN);

        // Also seed standard example domain admin accounts for convenience
        createUserIfNotFound("Admin One", "admin1@example.com", passwordHash, UserRole.ADMIN);
        createUserIfNotFound("Admin Two", "admin2@example.com", passwordHash, UserRole.ADMIN);
        createUserIfNotFound("Admin Three", "admin3@example.com", passwordHash, UserRole.ADMIN);
        createUserIfNotFound("Admin User", "admin@example.com", passwordHash, UserRole.ADMIN);

        User teacher = createUserIfNotFound("Ravi Teacher", "teacher@example.com", passwordHash, UserRole.USER);
        User student = createUserIfNotFound("Ananya Learner", "student@example.com", passwordHash, UserRole.USER);

        // 2. Create Categories safely
        Category catMB = createCategoryIfNotFound("Money & Business", "money-business",
                "Budget, saving, entrepreneurship, marketing", 4);
        Category catCom = createCategoryIfNotFound("Communication", "communication",
                "কথা বলা, presentation, customer handling", 3);
        Category catDig = createCategoryIfNotFound("Digital Skills", "digital-skills",
                "Computer, mobile, internet, AI ও online tools", 2);

        createCategoryIfNotFound("Basic Education", "basic-education", "ভাষা, গণিত, সাধারণ জ্ঞান, digital literacy", 1);
        createCategoryIfNotFound("Vocational Skills", "vocational-skills", "কারিগরি, repair, craft ও practical work",
                5);
        createCategoryIfNotFound("Creative Skills", "creative-skills", "Design, photography, writing, music, animation",
                6);
        createCategoryIfNotFound("Life Skills", "life-skills", "সময়, অভ্যাস, teamwork, problem solving", 7);
        createCategoryIfNotFound("Career Skills", "career-skills", "CV, interview, workplace, leadership", 8);
        createCategoryIfNotFound("Small Business", "small-business", "Product, service, pricing, sales, customer", 9);
        createCategoryIfNotFound("Rural Skills", "rural-skills", "কৃষি, food processing, local production", 10);
        createCategoryIfNotFound("Social Skills", "social-skills", "Community service, environment, civic learning",
                11);
        createCategoryIfNotFound("Advanced Academy", "advanced-academy",
                "Project, innovation, leadership ও creator economy", 12);

        // 3. Create Courses safely
        Course course1 = createCourseIfNotFound("BS-MB-101", "ব্যক্তিগত অর্থব্যবস্থা",
                "আয়, সঞ্চয়, বাজেট ও ছোট ব্যবসার মৌলিক শিক্ষা।", teacher, catMB, CourseDifficulty.BEGINNER);
        Course course2 = createCourseIfNotFound("BS-COM-101", "কথা বলার দক্ষতা",
                "Client, customer এবং team-এর সঙ্গে ভালোভাবে যোগাযোগ।", teacher, catCom, CourseDifficulty.ALL_LEVELS);
        Course course3 = createCourseIfNotFound("BS-DIG-101", "Digital Skill",
                "মোবাইল, web, online tools এবং digital work-এর ভিত্তি।", teacher, catDig, CourseDifficulty.BEGINNER);

        // 4. Create Lessons (only if course has 0 lessons to prevent duplication)
        if (lessonRepository.count() == 0) {
            createLesson(course1, "অর্থব্যবস্থার ভূমিকা", LessonType.VIDEO,
                    "https://www.youtube.com/watch?v=dQw4w9WgXcQ", null, 300, 1);
            createLesson(course1, "কীভাবে বাজেট তৈরি করবেন", LessonType.TEXT, null, "First text content", 0, 2);
            createLesson(course2, "যোগাযোগের মূলনীতি", LessonType.VIDEO, "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    null, 450, 1);
            createLesson(course3, "ইন্টারনেট ব্যবহার", LessonType.VIDEO, "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    null, 600, 1);
        }
    }

    private User createUserIfNotFound(String name, String email, String passwordHash, UserRole role) {
        return userRepository.findByEmail(email).map(user -> {
            if (role == UserRole.ADMIN) {
                user.setRole(UserRole.ADMIN);
            }
            user.setPasswordHash(passwordHash);
            user.setAccountStatus(UserStatus.ACTIVE);
            return userRepository.save(user);
        }).orElseGet(() -> {
            User user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setPasswordHash(passwordHash);
            user.setRole(role);
            user.setAccountStatus(UserStatus.ACTIVE);
            return userRepository.save(user);
        });
    }

    private Category createCategoryIfNotFound(String name, String slug, String desc, int order) {
        return categoryRepository.findBySlug(slug).orElseGet(() -> {
            Category cat = new Category();
            cat.setName(name);
            cat.setSlug(slug);
            cat.setDescription(desc);
            cat.setDisplayOrder(order);
            cat.setActive(true);
            return categoryRepository.save(cat);
        });
    }

    private Course createCourseIfNotFound(String code, String title, String desc, User teacher, Category cat,
            CourseDifficulty difficulty) {
        return courseRepository.findByCourseCode(code).orElseGet(() -> {
            Course course = new Course();
            course.setCourseCode(code);
            course.setTitle(title);
            course.setDescription(desc);
            course.setTeacher(teacher);
            course.setCategory(cat);
            course.setDifficulty(difficulty);
            course.setStatus(CourseStatus.PUBLISHED);
            course.setPublishedAt(ZonedDateTime.now());
            return courseRepository.save(course);
        });
    }

    private void createLesson(Course course, String title, LessonType type, String videoUrl, String content,
            int duration, int order) {
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(title);
        lesson.setLessonType(type);
        lesson.setVideoUrl(videoUrl);
        lesson.setContentText(content);
        lesson.setDurationSeconds(duration);
        lesson.setDisplayOrder(order);
        lessonRepository.save(lesson);
    }
}
