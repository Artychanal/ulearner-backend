package com.artur.java.ulearner.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
}