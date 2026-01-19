package com.example.demo.controller;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import com.example.demo.service.AccountCreator;
import com.example.demo.dto.AdminAccountCreator;
@RestController
@RequiredArgsConstructor
@RequestMapping("/moderator")
public class ModeratorController{
    private final AccountCreator acService;
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@RequestBody AdminAccountCreator accountCreator){
         try{ 
            acService.create(accountCreator);
                Map<String , String> map = new HashMap<>();
            map.put("message","Successfully create user");
        return ResponseEntity.ok(map);
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    }