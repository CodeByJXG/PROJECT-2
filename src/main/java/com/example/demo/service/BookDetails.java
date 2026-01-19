package com.example.demo.service;

import org.springframework.stereotype.Component;

import com.example.demo.dto.BookCreateResponse;
import com.example.demo.model.Book;

@Component
public class BookDetails{
    
    public BookCreateResponse getBook(Book book){
        return new BookCreateResponse()
        .setId(book.getId())
        .setTitle(book.getTitle())
        .setAuthor(book.getAuthor())
        .setStock(book.getStock())
        .setFilePath(book.getFilePath())
        .setLibrarianUsername(book.getLibrarianUsername())
        ;
    }
    
    }