package com.example.demo.dto;


import lombok.Data;

@Data
public class AdminAccountCreator{
    private String username;
    private String password;
    private String roles;
}