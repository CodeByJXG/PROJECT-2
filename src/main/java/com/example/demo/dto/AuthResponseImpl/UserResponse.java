package com.example.demo.dto.AuthResponseImpl;

import com.example.demo.dto.AuthResponse;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class UserResponse implements AuthResponse{
    private String message; // Message
    private String token;//JWT token
    private String roles;//ROLE_USER OR ROLE_ADMIN
}