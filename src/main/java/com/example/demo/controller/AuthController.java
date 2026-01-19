package com.example.demo.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.AuthRequest;
import com.example.demo.model.MyUser;
import com.example.demo.service.MyUserService;
import com.example.demo.dto.AuthResponse;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController{
    private final AuthenticationManager authenticationManager;
    private final MyUserService service;
    
    @PostMapping("/create")
    public ResponseEntity<?> createAuth(@RequestBody AuthRequest request){
        try{
        MyUser user = service.save(request);
        return ResponseEntity.ok(service.generateCreateResponse(request.getUsername()));
        }catch(Exception ex){
            Map<String,String> map = new HashMap<>();
            map.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
             }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authLogin(@RequestBody AuthRequest request) throws Exception{
        try{
             Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
SecurityContextHolder.getContext().setAuthentication(auth);
return ResponseEntity.ok(service.generateLoginResponse(auth));
        }catch(Exception ex){
            Map<String,String> map = new HashMap<>();
            map.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
    }
}