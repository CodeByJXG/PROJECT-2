package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.List;
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class AcceptedRequest{
    @Id
    @GeneratedValue
    private int id;
    @Column(unique=true)
    private String filePath;
    @ManyToOne
    @JoinColumn(name="user_id")
    private MyUser user;
    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;
    private String librarianUsername;
    @Column(nullable=false , unique=true)
    private int requestBookId;
    private int downloadAttemp;
    }