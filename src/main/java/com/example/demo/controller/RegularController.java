package com.example.demo.controller;


import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import com.example.demo.service.RegularUserService;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.example.demo.dto.RequestBookDto;
import org.springframework.http.HttpStatus;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.example.demo.dto.DownloadDto;

//Admin
@RestController
@RequestMapping("/regular")
@RequiredArgsConstructor
public class RegularController{
    private final RegularUserService service;
    @PostMapping("/requestBook/{id}")
    public ResponseEntity<?> requestesBooks(@RequestBody RequestBookDto dto ,@PathVariable int id , Authentication auth){
        try{     
            return ResponseEntity.ok(service.setRequest(dto,id,auth));
           }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
    @GetMapping("/getAllRequest")
    public ResponseEntity<?> getAllRequest(Authentication auth){
         try{     
            return ResponseEntity.ok(service.getRequest(auth));
           }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
        @GetMapping("/getUsersDataStatus")
    public ResponseEntity<?> getUserDataStatus(Authentication auth){
         try{     
            return ResponseEntity.ok(service.getUsersDataStatus(auth));
           }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
     @PostMapping("/returnRequest/{id}")
    public ResponseEntity<?> returnRequest(@PathVariable int id,Authentication auth){
         try{     
             Map<String , String> map = new HashMap<>();
             service.returnRequest(id,auth);
             map.put("message","successfully returned request");
            return ResponseEntity.ok(map);
           }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
        @DeleteMapping("/deleteRequest/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable int id,Authentication auth){
         try{     
             Map<String , String> map = new HashMap<>();
             service.deleteRequest(id,auth);
             map.put("message","successfully deleted request");
            return ResponseEntity.ok(map);
           }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
    
    @PostMapping("/download")
    public ResponseEntity<?> download(@RequestBody DownloadDto dto, Authentication auth)throws MalformedURLException{
        try{     
            String contentType = "application/octet-stream"; // default
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Download" + "\"")
                .body(service.getDownloadFile(dto.getAcceptReqId(),auth,dto.getLibrarianUsername()));
           }catch(Exception ex){
               ex.printStackTrace(); 
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
    }