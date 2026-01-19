package com.example.demo.service;

import java.util.ArrayList;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.dto.AuthRequest;
import com.example.demo.model.MyUser;
import com.example.demo.repo.MyRepository;
import com.example.demo.security.JwtProvider;

import java.util.List;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.AuthResponse;
import org.springframework.security.core.Authentication;
import com.example.demo.dto.AuthResponseImpl.AdminResponse;
import com.example.demo.dto.AuthResponseImpl.UserResponse;
import com.example.demo.dto.AuthResponseImpl.CreateResponse;

@Service
@RequiredArgsConstructor
public class MyUserService{
    private final MyRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    public MyUser save(AuthRequest request){
        return repository.save(new MyUser()
        .setUsername(request.getUsername())
        .setPassword(passwordEncoder.encode(request.getPassword()))
        .setRoles(List.of(Role.USER.toString()))
);
}
    public AuthResponse generateLoginResponse(Authentication auth){
        //TODO 
        String token = jwtProvider.genrateToken(((UserDetails) auth.getPrincipal()).getUsername());
        List<String> roles = auth.getAuthorities().stream()
                         .map(GrantedAuthority::getAuthority) // extract role name
                         .collect(Collectors.toList());
if (hasModeratorRole(roles)){
    return new AdminResponse().setMessage("Successfully Login as An MODERATOR").setToken(token).setRoles(Role.MODERATOR.toString());
}
 else if(hasAdminRole(roles)){
     return new AdminResponse().setMessage("Successfully Login as An Admin").setToken(token).setRoles(Role.ADMIN.toString());
 }
return new UserResponse().setMessage("Successfully Login as a User").setToken(token).setRoles(Role.USER.toString());
                
    }
    private boolean hasModeratorRole(List<String> roles){
        for(String r : roles){
            if(r.equals(Role.MODERATOR.toString())) return true;
            
        }
        return false;
    }
      
          private boolean hasAdminRole(List<String> roles){
        for(String r : roles){
            if(r.equals(Role.ADMIN.toString())) return true;
            
        }
        return false;
    }
    public AuthResponse generateCreateResponse(String username){
        return new CreateResponse().setMessage("Successfully created account"+username);
        }
    
 
}