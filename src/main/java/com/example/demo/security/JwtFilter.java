package com.example.demo.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.repo.MyRepository;
import com.example.demo.service.MyUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{
    private final JwtProvider provider;
    private final MyRepository repo;
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
            try{
	// TODO Auto-generated method stub
    String authHeader = request.getHeader("Authorization");
    if(authHeader==null||!authHeader.startsWith("Bearer ")){
        filterChain.doFilter(request, response);
        return;
    }
	String token = authHeader.substring(7);
    String username = provider.getUsernameFromToken(token);
    UserDetails details = new MyUserDetails(repo.findByUsername(username).get());
    if(details!=null&&provider.validateToken(token)&& SecurityContextHolder.getContext().getAuthentication()==null){
        UsernamePasswordAuthenticationToken auth = new 
UsernamePasswordAuthenticationToken(details, null,details.getAuthorities()); 
SecurityContextHolder.getContext().setAuthentication(auth);
}}catch(Exception ex){
    System.out.println("Something went Wrong "+ ex.getMessage());
}
    filterChain.doFilter(request, response);
    
    
    }}