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
import org.springframework.security.core.Authentication;
import com.example.demo.dto.BookCreateRequest;
import com.example.demo.dto.BookCreateResponse;
import com.example.demo.security.JwtProvider;
import com.example.demo.service.ManagerService;
import com.example.demo.security.MyUserDetails;
import com.example.demo.dto.LibrarianDashboardDto;
import com.example.demo.dto.LibrarianUsernameDto;
import lombok.RequiredArgsConstructor;
//Admin
@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManageController{
    private final JwtProvider provider;
    private final ManagerService service;
    
    
    
    
    
    
        @PostMapping("/setLibrarianUsername")
    public ResponseEntity<?> setLibrarianUsername(@RequestBody LibrarianUsernameDto dto, Authentication auth){
        try{ 
            String username = dto.getLibrarianUsername();
            service.setLibrarianUsername(auth,username);
                Map<String , String> map = new HashMap<>();
            map.put("message","Successfully set Librarian username");
        return ResponseEntity.ok(map);
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
    
                @GetMapping("/getLibrarianDataStatus")
    public ResponseEntity<?> getLibrarianDataStatus(Authentication auth){
           Map<String , String> map = new HashMap<>();
        try{
            
        return ResponseEntity.ok(service.getLibrarianDataStatus(auth));
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
                @GetMapping("/getLibrarianUsername")
    public ResponseEntity<?> getLibrarianUsername(Authentication auth){
           Map<String , String> map = new HashMap<>();
        try{
            String librarianUsername= service.getLibrarianUsername(auth);
            map.put("message",librarianUsername);
        return ResponseEntity.ok(map);
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
    
    
    
    
    
    
    @PostMapping("/createBook")
    public ResponseEntity<?> createBook
    (@ModelAttribute BookCreateRequest bookCreateRequest,
    @RequestParam("pdfFile")MultipartFile pdfFile,Authentication auth)
    {
        try{
        String username = auth.getName();
        BookCreateResponse bookCreateResponse = service.saveBook(username,bookCreateRequest,pdfFile);
        System.out.println("Succeed : "+bookCreateResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCreateResponse);
        }catch(Exception ex){
            System.out.println("Error : "+ ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    @GetMapping("/getAllBooks")
    public ResponseEntity<List<BookCreateResponse>> getAllBooks(Authentication auth){
          String username = auth.getName();
          return ResponseEntity.ok().body(service.getAllBooks(username));
    }
    
    @PutMapping("/updateBook/{id}")
    public ResponseEntity<?> updateBooks(@ModelAttribute BookCreateRequest bookCreateRequest,
    @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,Authentication auth,@PathVariable int id){
        try{
        String username = auth.getName();
      return ResponseEntity.ok(service.updateBook(username,bookCreateRequest,pdfFile,id));}catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    @DeleteMapping("/deleteBook/{id}")
public ResponseEntity<?> deleteBook(@PathVariable int id, Authentication auth) {
    // Your logic to delete the book by id, optionally checking the authenticated user
    String username = auth.getName();
    service.deleteBooks(username,id);
   Map<String , String> map = new HashMap<>();
            map.put("message","Successfully deleted");
            return ResponseEntity.ok(map);
}



    @PostMapping("/requestBookAccept/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable int id, Authentication auth){
        try{ 
        return ResponseEntity.ok(service.setRequestAccept(id,auth));
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            Map<String , String> map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    @DeleteMapping("/deleteRequestBook/{id}")
    public ResponseEntity<?> deleteRequestBook(@PathVariable int id, Authentication auth){
           Map<String , String> map = new HashMap<>();
        try{
            
            service.deleteRequestBook(id,auth);
            map.put("meesage","Successfully deleted book");
        return ResponseEntity.ok(map);
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
            @GetMapping("/getAllRequestBook")
    public ResponseEntity<?> getAllRequest(Authentication auth){
           Map<String , String> map = new HashMap<>();
        try{
            
        return ResponseEntity.ok(service.getAllRequestBook(auth));
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    
        @PostMapping("/requestBookDenied/{id}")
    public ResponseEntity<?> declineRequest(@PathVariable int id, Authentication auth){
        Map<String , String> map = new HashMap<>();
        try{
            
            service.setRequestDenied(id,auth);
            map.put("meesage","Successfully denied book");
        return ResponseEntity.ok(map);
        }catch(Exception ex){
            System.out.println("Error : " +ex.getMessage());
            map = new HashMap<>();
            map.put("message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
        }
    }
    
    }