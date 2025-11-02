package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {
    private Long id;
    private Long userId;
    private Long lessonId;
    private Boolean completed;
    private Integer progressPercentage;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
