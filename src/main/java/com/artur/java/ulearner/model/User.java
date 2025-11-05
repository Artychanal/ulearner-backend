package com.artur.java.ulearner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.STUDENT;

    private String avatarUrl;
    private String bio;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "instructor")
    @Builder.Default
    @JsonIgnore
    private Set<Course> instructedCourses = new HashSet<>();

    @ManyToMany(mappedBy = "enrolledStudents", fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnore
    private Set<Course> enrolledCourses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    @JsonIgnore
    private Set<Progress> progressRecords = new HashSet<>();
}