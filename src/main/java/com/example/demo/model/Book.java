package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;
    
    private String librarianUsername;
    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int stock;
    
    @ManyToOne
    @JoinColumn(name = "librarian_id", nullable = false)
    private Librarian librarian;

    // Path of PDF stored on the server
    @Column(nullable = false)
    private String filePath;
    
    
}