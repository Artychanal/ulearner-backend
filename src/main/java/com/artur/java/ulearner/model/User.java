package com.artur.java.ulearner.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT;

    private String avatarUrl;
    private String bio;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "instructor")
    private Set<Course> instructedCourses = new HashSet<>();

    @ManyToMany(mappedBy = "enrolledStudents")
    private Set<Course> enrolledCourses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Progress> progressRecords = new HashSet<>();
}