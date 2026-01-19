package com.example.demo.service;

public enum Role{
    MODERATOR,
    ADMIN,
    USER;
    @Override
    public String toString(){
        return "ROLE_"+name();
    }
}