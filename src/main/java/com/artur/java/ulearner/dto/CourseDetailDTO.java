package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String difficulty;
    private Integer duration;
    private String updateFrequency;
    private Set<String> features;
    private UserDTO instructor;
    private Set<LessonDTO> lessons;
    private Integer enrolledCount;
    private Boolean published;
    private Boolean isEnrolled;
    private Double completionPercentage;
    private LocalDateTime createdAt;
}