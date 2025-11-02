package com.artur.java.ulearner.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String content;
    private Integer orderIndex;
    private Integer duration;
    private String videoUrl;
}