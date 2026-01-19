package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Librarian;
import com.example.demo.model.MyUser;

@Repository
public interface MyLibrarianRepository extends JpaRepository<Librarian,Integer>{
    Librarian findByUserUsername(String username);
    Librarian findByLibrarianUsername(String librarianUsername);
}