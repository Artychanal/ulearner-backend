package com.artur.java.ulearner.config;

import com.artur.java.ulearner.model.*;
import com.artur.java.ulearner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        User instructor = User.builder()
                .email("john.doe@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.INSTRUCTOR)
                .avatarUrl("https://i.pravatar.cc/150?img=12")
                .bio("Experienced web developer and educator")
                .build();
        userRepository.save(instructor);

        User student = User.builder()
                .email("student@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.STUDENT)
                .build();
        userRepository.save(student);

        Course course = Course.builder()
                .title("Introduction to Next.js")
                .description("Learn the fundamentals of Next.js and build modern web applications.")
                .imageUrl("https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800")
                .difficulty("Beginner")
                .duration(8)
                .updateFrequency("monthly")
                .instructor(instructor)
                .published(true)
                .features(Set.of(
                        "Lifetime access",
                        "Project templates",
                        "Mentor feedback",
                        "Certificate of completion"
                ))
                .build();
        courseRepository.save(course);

        Lesson lesson1 = Lesson.builder()
                .title("Getting Started with Next.js")
                .content("Introduction to Next.js framework and setup")
                .orderIndex(1)
                .duration(45)
                .videoUrl("https://example.com/video1")
                .course(course)
                .build();
        lessonRepository.save(lesson1);

        Lesson lesson2 = Lesson.builder()
                .title("Pages and Routing")
                .content("Understanding Next.js routing system")
                .orderIndex(2)
                .duration(60)
                .videoUrl("https://example.com/video2")
                .course(course)
                .build();
        lessonRepository.save(lesson2);

        Lesson lesson3 = Lesson.builder()
                .title("Data Fetching")
                .content("Learn SSR, SSG, and ISR in Next.js")
                .orderIndex(3)
                .duration(75)
                .videoUrl("https://example.com/video3")
                .course(course)
                .build();
        lessonRepository.save(lesson3);

        Course course2 = Course.builder()
                .title("Advanced React Patterns")
                .description("Master advanced React patterns and best practices for building scalable applications.")
                .imageUrl("https://images.unsplash.com/photo-1633356122102-3fe601e05bd2?w=800")
                .difficulty("Advanced")
                .duration(12)
                .updateFrequency("monthly")
                .instructor(instructor)
                .published(true)
                .features(Set.of(
                        "Real-world projects",
                        "Code reviews",
                        "Community access"
                ))
                .build();
        courseRepository.save(course2);

        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("👨‍🏫 Instructor: john.doe@example.com / password123");
        System.out.println("👨‍🎓 Student: student@example.com / password123");
    }
}