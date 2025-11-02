package com.artur.java.ulearner.controller;

import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<List<LessonDTO>> getCourseLessons(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(lessonService.getCourseLessons(courseId, email));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonById(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(lessonService.getLessonById(lessonId, email));
    }

    @PostMapping
    public ResponseEntity<LessonDTO> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonService.createLesson(courseId, request, authentication.getName()));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, request, authentication.getName()));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        lessonService.deleteLesson(lessonId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}