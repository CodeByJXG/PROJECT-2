package com.example.demo.dto.AuthResponseImpl;

import com.example.demo.dto.AuthResponse;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class CreateResponse implements AuthResponse{
    private String message;
}