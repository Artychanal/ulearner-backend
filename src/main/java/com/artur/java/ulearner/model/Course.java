package com.artur.java.ulearner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String imageUrl;
    private String difficulty;
    private Integer duration;
    private String updateFrequency;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonIgnore
    private User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private Set<Lesson> lessons = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "course_enrollments",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<User> enrolledStudents = new HashSet<>();

    @ElementCollection
    @Builder.Default
    private Set<String> features = new HashSet<>();

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    private Boolean published = false;
}