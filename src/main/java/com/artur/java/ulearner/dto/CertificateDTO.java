package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private Long id;
    private String certificateNumber;
    private UserDTO user;
    private CourseDTO course;
    private LocalDateTime issuedAt;
    private LocalDateTime completedAt;
    private String pdfUrl;
    private Boolean verified;
}