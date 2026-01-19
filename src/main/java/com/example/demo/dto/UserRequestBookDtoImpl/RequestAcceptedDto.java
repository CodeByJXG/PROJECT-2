package com.example.demo.dto.UserRequestBookDtoImpl;


import com.example.demo.dto.UserRequestBookDto;
import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain=true)
public class RequestAcceptedDto implements UserRequestBookDto{
    private int requestId;
    private String title;
    private String author;
    private int stock;
    private String librarianUsername;
    private String requestStatus;
    //Todo {Make this file path}
    private String fileDownloadPath;
    private int downloadAttempLeft;
}