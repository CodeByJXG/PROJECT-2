
package com.example.demo.dto;


import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain=true)
public class BookCreateResponse{
    private int id;
    private String title;
    private String author;
    private int stock;
    private String filePath;
    private String librarianUsername;
}