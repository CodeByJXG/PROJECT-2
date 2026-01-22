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
public class RequestBook{
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private MyUser user;
    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;
    private String librarianUsername;
    private String reqStatus;
    }