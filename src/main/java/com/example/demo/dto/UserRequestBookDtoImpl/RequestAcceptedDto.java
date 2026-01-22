package com.example.demo.dto.UserRequestBookDtoImpl;

//Required change in frontend ❌

import com.example.demo.dto.UserRequestBookDto;
import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain=true)
public class RequestAcceptedDto implements UserRequestBookDto{
    private int requestId;
    private int acceptReqId;
    private String title;
    private String author;
    private int stock;
    private String librarianUsername;
    private String requestStatus;
    //Todo {Make this file path}
    private int downloadAttempLeft;
}