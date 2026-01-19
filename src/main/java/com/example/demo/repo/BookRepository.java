package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.demo.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer>{
    Book findByFilePath(String filePath);
    Long countByStockGreaterThan(int stock);
    List<Book> findByLibrarianUsername(String librarianUsername);
}