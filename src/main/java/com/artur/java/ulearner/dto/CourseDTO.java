package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String difficulty;
    private Integer duration;
    private String updateFrequency;
    private Set<String> features;
    private UserDTO instructor;
    private Integer lessonsCount;
    private Integer enrolledCount;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}