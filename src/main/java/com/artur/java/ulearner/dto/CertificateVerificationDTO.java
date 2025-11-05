package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateVerificationDTO {
    private Boolean valid;
    private String certificateNumber;
    private String studentName;
    private String courseName;
    private LocalDateTime issuedAt;
    private String message;
}