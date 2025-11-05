package com.artur.java.ulearner.service;

import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.model.*;
import com.artur.java.ulearner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ProgressRepository progressRepository;

    public List<CourseDTO> getPublishedCourses() {
        return courseRepository.findByPublished(true).stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }

    public CourseDetailDTO getCourseDetail(Long id, String userEmail) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean isEnrolled = false;
        Double completionPercentage = 0.0;

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                isEnrolled = course.getEnrolledStudents().contains(user);
                completionPercentage = progressRepository.getCourseCompletionPercentage(user.getId(), id);
                if (completionPercentage == null) completionPercentage = 0.0;
            }
        }

        return CourseDetailDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .difficulty(course.getDifficulty())
                .duration(course.getDuration())
                .updateFrequency(course.getUpdateFrequency())
                .features(course.getFeatures())
                .instructor(mapToUserDTO(course.getInstructor()))
                .lessons(course.getLessons().stream()
                        .map(this::mapToLessonDTO)
                        .collect(Collectors.toSet()))
                .enrolledCount(course.getEnrolledStudents().size())
                .published(course.getPublished())
                .isEnrolled(isEnrolled)
                .completionPercentage(completionPercentage)
                .createdAt(course.getCreatedAt())
                .build();
    }

    public List<CourseDTO> searchCourses(String keyword) {
        return courseRepository.searchPublishedCourses(keyword).stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(CourseCreateRequest request, String instructorEmail) {
        var instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        var course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .difficulty(request.getDifficulty())
                .duration(request.getDuration())
                .updateFrequency(request.getUpdateFrequency())
                .features(request.getFeatures())
                .instructor(instructor)
                .build();

        courseRepository.save(course);
        return mapToCourseDTO(course);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseCreateRequest request, String instructorEmail) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setImageUrl(request.getImageUrl());
        course.setDifficulty(request.getDifficulty());
        course.setDuration(request.getDuration());
        course.setUpdateFrequency(request.getUpdateFrequency());
        course.setFeatures(request.getFeatures());
        course.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(course);
        return mapToCourseDTO(course);
    }

    @Transactional
    public CourseDTO publishCourse(Long id, String instructorEmail) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        course.setPublished(true);
        courseRepository.save(course);
        return mapToCourseDTO(course);
    }

    @Transactional
    public void enrollStudent(Long courseId, String studentEmail) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        course.getEnrolledStudents().add(student);
        student.getEnrolledCourses().add(course);

        courseRepository.save(course);
        userRepository.save(student);
    }

    public List<CourseDTO> getUserEnrolledCourses(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getEnrolledCourses().stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getInstructorCourses(String instructorEmail) {
        var instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        return courseRepository.findByInstructorId(instructor.getId()).stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
    }

    private CourseDTO mapToCourseDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .difficulty(course.getDifficulty())
                .duration(course.getDuration())
                .updateFrequency(course.getUpdateFrequency())
                .features(course.getFeatures())
                .instructor(mapToUserDTO(course.getInstructor()))
                .lessonsCount(course.getLessons().size())
                .enrolledCount(course.getEnrolledStudents().size())
                .published(course.getPublished())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private LessonDTO mapToLessonDTO(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .duration(lesson.getDuration())
                .videoUrl(lesson.getVideoUrl())
                .courseId(lesson.getCourse().getId())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}