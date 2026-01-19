package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class RequestBooksUserAdmin{
    
    private int requestBookId;
    private String userUsername;
    private String bookTitle;
    private String bookAuthor;
    private String bookStatus;
    }