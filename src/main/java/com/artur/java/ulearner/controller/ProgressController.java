package com.artur.java.ulearner.controller;

import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/lessons/{lessonId}")
    public ResponseEntity<ProgressDTO> updateProgress(
            @PathVariable Long lessonId,
            @Valid @RequestBody ProgressUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(progressService.updateProgress(
                authentication.getName(), lessonId, request
        ));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Double> getCourseProgress(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(progressService.getCourseProgress(
                authentication.getName(), courseId
        ));
    }

    @GetMapping("/my-progress")
    public ResponseEntity<List<ProgressDTO>> getMyProgress(Authentication authentication) {
        return ResponseEntity.ok(progressService.getUserProgress(authentication.getName()));
    }
}