package com.codewithmosh.store.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name must not be null or empty")
    private String name;
    @Email(message = "Email must be in a correct email format")
    private String email;
    @Size(min = 6, message="Password must be at least 6 characters")
    private String password;
}
