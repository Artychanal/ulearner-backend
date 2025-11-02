package com.artur.java.ulearner.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String imageUrl;
    private String difficulty;
    private Integer duration;
    private String updateFrequency;
    private Set<String> features;
}
