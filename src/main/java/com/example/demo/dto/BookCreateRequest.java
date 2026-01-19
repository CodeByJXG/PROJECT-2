package com.example.demo.dto;

import lombok.Data;

@Data
public class BookCreateRequest{
    private String title;
    private String author;
    private int stock;
    }
    