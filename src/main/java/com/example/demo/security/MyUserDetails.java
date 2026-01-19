package com.example.demo.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.demo.model.MyUser;
public class MyUserDetails implements UserDetails{
    private final MyUser user;
    MyUserDetails(MyUser user){
        this.user=user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Sample To not getting lost
        //List with an Object
      /*  return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
                
                   return Collections.emptyList();*/
                //List with a string
                return user.getRoles().stream()
           .map(role -> new SimpleGrantedAuthority(role))
           .toList();
             
    }
    public int getId(){
        return user.getId();
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}