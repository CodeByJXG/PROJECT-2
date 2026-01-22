package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "author", nullable = false)
    private String author;
    
    // REMOVE @Lob - just use BYTEA column definition
    @Column(name = "byte_data", columnDefinition = "BYTEA", nullable = false)
    private byte[] data;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "librarian_id", nullable = false)
    private Librarian librarian;
    
    @Column(name = "librarian_username")
    private String librarianUsername;
    
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    @Column(name = "title", nullable = false)
    private String title;
}