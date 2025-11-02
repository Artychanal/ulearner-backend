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
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public ProgressDTO updateProgress(String userEmail, Long lessonId, ProgressUpdateRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        var progress = progressRepository.findByUserIdAndLessonId(user.getId(), lessonId)
                .orElse(Progress.builder()
                        .user(user)
                        .lesson(lesson)
                        .build());

        progress.setCompleted(request.getCompleted());
        progress.setProgressPercentage(request.getProgressPercentage());
        progress.setLastAccessedAt(LocalDateTime.now());

        if (request.getCompleted() && progress.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        }

        progressRepository.save(progress);
        return mapToProgressDTO(progress);
    }

    public Double getCourseProgress(String userEmail, Long courseId) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double percentage = progressRepository.getCourseCompletionPercentage(user.getId(), courseId);
        return percentage != null ? percentage : 0.0;
    }

    public List<ProgressDTO> getUserProgress(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return progressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToProgressDTO)
                .collect(Collectors.toList());
    }

    private ProgressDTO mapToProgressDTO(Progress progress) {
        return ProgressDTO.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .lessonId(progress.getLesson().getId())
                .completed(progress.getCompleted())
                .progressPercentage(progress.getProgressPercentage())
                .completedAt(progress.getCompletedAt())
                .lastAccessedAt(progress.getLastAccessedAt())
                .build();
    }
}