package com.artur.java.ulearner.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.artur.java.ulearner.AbstractIntegrationTest;
import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.model.*;
import com.artur.java.ulearner.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CertificateIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentToken;
    private Long courseId;
    private Long studentId;
    private User instructor;
    private User student;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up in correct order
        certificateRepository.deleteAll();
        progressRepository.deleteAll();
        lessonRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create instructor
        instructor = User.builder()
                .email("instructor@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.INSTRUCTOR)
                .instructedCourses(new HashSet<>())
                .enrolledCourses(new HashSet<>())
                .progressRecords(new HashSet<>())
                .build();
        instructor = userRepository.save(instructor);

        // Create student
        student = User.builder()
                .email("student@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.STUDENT)
                .instructedCourses(new HashSet<>())
                .enrolledCourses(new HashSet<>())
                .progressRecords(new HashSet<>())
                .build();
        student = userRepository.save(student);
        studentId = student.getId();

        // Create course with lessons
        Course course = Course.builder()
                .title("Certificate Course")
                .description("Description")
                .instructor(instructor)
                .published(true)
                .lessons(new HashSet<>())
                .enrolledStudents(new HashSet<>())
                .features(new HashSet<>())
                .build();
        course = courseRepository.save(course);
        courseId = course.getId();

        // Create 3 lessons
        for (int i = 1; i <= 3; i++) {
            Lesson lesson = Lesson.builder()
                    .title("Lesson " + i)
                    .content("Content " + i)
                    .orderIndex(i)
                    .course(course)
                    .progressRecords(new HashSet<>())
                    .build();
            lessonRepository.save(lesson);
        }

        // Login student
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("student@test.com");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthResponse.class
        );
        studentToken = authResponse.getToken();

        // Enroll in course
        mockMvc.perform(post("/courses/" + courseId + "/enroll")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    void generateCertificate_Success() throws Exception {
        // Complete all lessons
        var lessons = lessonRepository.findByCourseIdOrderByOrderIndex(courseId);
        for (Lesson lesson : lessons) {
            ProgressUpdateRequest progressRequest = new ProgressUpdateRequest();
            progressRequest.setCompleted(true);
            progressRequest.setProgressPercentage(100);

            mockMvc.perform(post("/progress/lessons/" + lesson.getId())
                            .header("Authorization", "Bearer " + studentToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(progressRequest)))
                    .andExpect(status().isOk());
        }

        // Generate certificate
        mockMvc.perform(post("/certificates/generate/" + courseId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificateNumber").exists())
                .andExpect(jsonPath("$.user.email").value("student@test.com"))
                .andExpect(jsonPath("$.course.title").value("Certificate Course"));
    }

    @Test
    void generateCertificate_NotCompleted_ReturnsBadRequest() throws Exception {
        // Try to generate without completing
        mockMvc.perform(post("/certificates/generate/" + courseId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyCertificate_Success() throws Exception {
        // Create certificate manually for verification
        User studentFromDb = userRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Certificate certificate = Certificate.builder()
                .certificateNumber("UL-TEST123456")
                .user(studentFromDb)
                .course(course)
                .verified(true)
                .build();
        certificateRepository.save(certificate);

        mockMvc.perform(get("/certificates/verify/UL-TEST123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.certificateNumber").value("UL-TEST123456"))
                .andExpect(jsonPath("$.studentName").value("Jane Smith"));
    }
}
