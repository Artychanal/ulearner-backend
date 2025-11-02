package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private Long id;
    private String title;
    private String content;
    private Integer orderIndex;
    private Integer duration;
    private String videoUrl;
    private Long courseId;
    private Boolean completed;
    private Integer progressPercentage;
    private LocalDateTime createdAt;
}