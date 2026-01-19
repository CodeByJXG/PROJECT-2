package com.example.demo.security;
//Required Imports
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//Custom Required Imports
import com.example.demo.model.MyUser;
import com.example.demo.repo.MyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService{
    private final MyRepository repository;
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	// TODO Auto-generated method stub
    MyUser user = repository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username Notfound"));
	return new MyUserDetails(user);
}}