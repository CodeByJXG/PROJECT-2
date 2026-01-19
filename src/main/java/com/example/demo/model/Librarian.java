package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "librarians")
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String librarianUsername;

    // Link to MyUser (1-to-1)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;

    // Books managed by this librarian
    @OneToMany(mappedBy = "librarian", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Book> books;
    
}