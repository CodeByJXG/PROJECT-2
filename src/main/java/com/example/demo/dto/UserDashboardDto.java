package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserDashboardDto{
    private long totalBooks;
    private Long availableBooks;
    private Long myBorrowedBooks;
    private Long myPendingRequest;
    }