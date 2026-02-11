package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    @NotBlank
    private String password;
}
