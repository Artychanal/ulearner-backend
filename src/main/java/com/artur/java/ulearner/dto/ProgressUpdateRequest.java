package com.artur.java.ulearner.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {
    private Boolean completed;
    private Integer progressPercentage;
}