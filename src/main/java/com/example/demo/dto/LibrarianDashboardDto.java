package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class LibrarianDashboardDto{
    private long totalUsers;
    private long totalLibrarians;
    private long totalBooks;
    private Long booksApprove;
    }