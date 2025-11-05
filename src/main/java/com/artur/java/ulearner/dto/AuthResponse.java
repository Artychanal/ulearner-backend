package com.artur.java.ulearner.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;

    @Builder.Default
    private String type = "Bearer";

    private UserDTO user;
}
