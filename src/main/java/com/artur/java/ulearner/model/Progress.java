package com.artur.java.ulearner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    @JsonIgnore
    private Lesson lesson;

    @Builder.Default
    private Boolean completed = false;

    @Builder.Default
    private Integer progressPercentage = 0;

    private LocalDateTime completedAt;

    @Builder.Default
    private LocalDateTime lastAccessedAt = LocalDateTime.now();
}