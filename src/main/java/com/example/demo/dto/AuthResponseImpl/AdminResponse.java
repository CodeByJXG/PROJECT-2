package com.example.demo.dto.AuthResponseImpl;

import com.example.demo.dto.AuthResponse;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain=true)
public class AdminResponse implements AuthResponse{
    private String message;
    private String token;
    private String roles;
}