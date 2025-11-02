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
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ProgressRepository progressRepository;

    public List<LessonDTO> getCourseLessons(Long courseId, String userEmail) {
        var lessons = lessonRepository.findByCourseIdOrderByOrderIndex(courseId);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                return lessons.stream()
                        .map(lesson -> {
                            var progress = progressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId());
                            return mapToLessonDTO(lesson, progress.orElse(null));
                        })
                        .collect(Collectors.toList());
            }
        }

        return lessons.stream()
                .map(lesson -> mapToLessonDTO(lesson, null))
                .collect(Collectors.toList());
    }

    public LessonDTO getLessonById(Long lessonId, String userEmail) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Progress progress = null;
        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                progress = progressRepository.findByUserIdAndLessonId(user.getId(), lessonId).orElse(null);
            }
        }

        return mapToLessonDTO(lesson, progress);
    }

    @Transactional
    public LessonDTO createLesson(Long courseId, LessonCreateRequest request, String instructorEmail) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equals(instructorEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        var lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .orderIndex(request.getOrderIndex())
                .duration(request.getDuration())
                .videoUrl(request.getVideoUrl())
                .course(course)
                .build();

        lessonRepository.save(lesson);
        return mapToLessonDTO(lesson, null);
    }

    @Transactional
    public LessonDTO updateLesson(Long lessonId, LessonCreateRequest request, String instructorEmail) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (!lesson.getCourse().getInstructor().getEmail().equals(instructorEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setDuration(request.getDuration());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setUpdatedAt(LocalDateTime.now());

        lessonRepository.save(lesson);
        return mapToLessonDTO(lesson, null);
    }

    @Transactional
    public void deleteLesson(Long lessonId, String instructorEmail) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (!lesson.getCourse().getInstructor().getEmail().equals(instructorEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        lessonRepository.delete(lesson);
    }

    private LessonDTO mapToLessonDTO(Lesson lesson, Progress progress) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .duration(lesson.getDuration())
                .videoUrl(lesson.getVideoUrl())
                .courseId(lesson.getCourse().getId())
                .completed(progress != null ? progress.getCompleted() : false)
                .progressPercentage(progress != null ? progress.getProgressPercentage() : 0)
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}