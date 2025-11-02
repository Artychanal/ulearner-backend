package com.artur.java.ulearner.controller;

import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/public")
    public ResponseEntity<List<CourseDTO>> getPublicCourses() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<CourseDetailDTO> getCourseById(@PathVariable Long id, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(courseService.getCourseDetail(id, email));
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String keyword) {
        return ResponseEntity.ok(courseService.searchCourses(keyword));
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.createCourse(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, authentication.getName()));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseDTO> publishCourse(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(courseService.publishCourse(id, authentication.getName()));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<Void> enrollInCourse(@PathVariable Long id, Authentication authentication) {
        courseService.enrollStudent(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseDTO>> getMyCourses(Authentication authentication) {
        return ResponseEntity.ok(courseService.getUserEnrolledCourses(authentication.getName()));
    }

    @GetMapping("/instructor/courses")
    public ResponseEntity<List<CourseDTO>> getInstructorCourses(Authentication authentication) {
        return ResponseEntity.ok(courseService.getInstructorCourses(authentication.getName()));
    }
}