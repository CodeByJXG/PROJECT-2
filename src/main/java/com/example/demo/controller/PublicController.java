package com.example.demo.controller;

import com.example.demo.service.PublicService;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import com.example.demo.dto.BookCreateRequest;
import com.example.demo.dto.BookCreateResponse;
import com.example.demo.security.JwtProvider;
import com.example.demo.service.ManagerService;
import com.example.demo.dto.PublicBookResponse;

import lombok.RequiredArgsConstructor;
//Admin
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController{
    private final PublicService service;
    @GetMapping("/getAllBooks")
    public ResponseEntity<?> getAllBooks(Authentication auth){
        try{
        return ResponseEntity.ok(service.getAllBooks(auth));
        }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
    }