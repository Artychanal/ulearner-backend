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
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String instructorToken;
    private String studentToken;
    private User instructor;

    @BeforeEach
    void setUp() throws Exception {
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
        userRepository.save(instructor);

        // Create student
        User student = User.builder()
                .email("student@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.STUDENT)
                .instructedCourses(new HashSet<>())
                .enrolledCourses(new HashSet<>())
                .progressRecords(new HashSet<>())
                .build();
        userRepository.save(student);

        // Login instructor
        LoginRequest instructorLogin = new LoginRequest();
        instructorLogin.setEmail("instructor@test.com");
        instructorLogin.setPassword("password123");

        MvcResult instructorResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorLogin)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse instructorAuth = objectMapper.readValue(
                instructorResult.getResponse().getContentAsString(),
                AuthResponse.class
        );
        instructorToken = instructorAuth.getToken();

        // Login student
        LoginRequest studentLogin = new LoginRequest();
        studentLogin.setEmail("student@test.com");
        studentLogin.setPassword("password123");

        MvcResult studentResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentLogin)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse studentAuth = objectMapper.readValue(
                studentResult.getResponse().getContentAsString(),
                AuthResponse.class
        );
        studentToken = studentAuth.getToken();
    }

    @Test
    void createCourse_Success() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setTitle("Test Course");
        request.setDescription("Test Description");
        request.setDifficulty("Beginner");
        request.setFeatures(Set.of("Lifetime access", "Certificate"));

        mockMvc.perform(post("/courses")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void getPublicCourses_Success() throws Exception {
        // Create and publish course
        Course course = Course.builder()
                .title("Public Course")
                .description("Description")
                .instructor(instructor)
                .published(true)
                .lessons(new HashSet<>())
                .enrolledStudents(new HashSet<>())
                .features(new HashSet<>())
                .build();
        courseRepository.save(course);

        mockMvc.perform(get("/courses/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Public Course"));
    }

    @Test
    void enrollInCourse_Success() throws Exception {
        // Create and publish course
        Course course = Course.builder()
                .title("Enrollment Course")
                .description("Description")
                .instructor(instructor)
                .published(true)
                .lessons(new HashSet<>())
                .enrolledStudents(new HashSet<>())
                .features(new HashSet<>())
                .build();
        course = courseRepository.save(course);

        // Enroll in course
        mockMvc.perform(post("/courses/" + course.getId() + "/enroll")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        // Verify enrollment - check that we have at least one course
        mockMvc.perform(get("/courses/my-courses")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Enrollment Course"));
    }
}