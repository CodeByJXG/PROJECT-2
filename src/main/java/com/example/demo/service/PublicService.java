package com.example.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.example.demo.repo.BookRepository;
import com.example.demo.dto.PublicBookResponse;
import com.example.demo.dto.PublicBookResponseImpl.PublicAdminBookResponse;
import com.example.demo.dto.PublicBookResponseImpl.PublicUserBookResponse;
import com.example.demo.model.Book;
import java.util.List;           // for List
import java.util.stream.Collectors; // for Collectors.toList() if you use it
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.example.demo.repo.RequestBookRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.model.RequestBook;
import com.example.demo.security.MyUserDetails;
@Service
@RequiredArgsConstructor
public class PublicService {
    private final BookRepository bookRepo;
    private final RequestBookRepository repo;
    public List<? extends PublicBookResponse> getAllBooks(Authentication auth){
        List<Book>listOfBooks = bookRepo.findAll();
        if(listOfBooks==null){
            return List.of();
        }
        List<? extends PublicBookResponse> convertedBooks;
        List<String> roles = extractRoles(auth);
        
        if(hasAdminRole(roles)){
      convertedBooks  =  listOfBooks.stream()
        .map(b -> new PublicAdminBookResponse()
        .setId(b.getId())
        .setTitle(b.getTitle())
        .setAuthor(b.getAuthor())
        .setStock(b.getStock())
        .setLibrarianUsername(b.getLibrarianUsername())
        ).toList ();
        }else{
            //Todo Make the entity
            int id = getUserId(auth);
            List<RequestBook> currentRequestBooks = getCurrenReqBooks(id);
            Map<Integer, RequestBook> organizedMap = mapRequest(currentRequestBooks);
            convertedBooks  =  listOfBooks.stream()
        .map(b -> {RequestBook reqBook = organizedMap.get(b.getId());
        return new PublicUserBookResponse()
        .setId(b.getId())
        .setTitle(b.getTitle())
        .setAuthor(b.getAuthor())
        .setStock(b.getStock())
        .setLibrarianUsername(b.getLibrarianUsername())
        .setRequestStatus(reqBook==null ? "NOT_REQUESTED":reqBook.getReqStatus());
        }).toList ();
        }
        return convertedBooks;
    }
    private boolean hasAdminRole(List<String> roles){
        for(String r : roles){
            if(r.equals(Role.ADMIN.toString())) return true;
        }
        return false;
    }
    
    
    private List<RequestBook> getCurrenReqBooks(int id){
        return repo.findByUserId(id);
    }
    
    
    
    private Map<Integer, RequestBook> mapRequest(List<RequestBook> currentUserReqBooks){
        if(currentUserReqBooks==null) return new HashMap<>();
        return  currentUserReqBooks.stream().collect(Collectors.toMap(r->r.getBook().getId(),r->r));
    }
        private int getUserId(Authentication auth){
            if(auth==null){
                throw new IllegalStateException("Authentication is null");
            }
            Object principal = auth.getPrincipal();
        if(principal instanceof MyUserDetails){
            MyUserDetails userDetails = (MyUserDetails) principal ;
            return userDetails.getId();
        }
        throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
    }
    
    private List<String> extractRoles(Authentication auth){
        List<String> roles = auth.getAuthorities().stream()
                         .map(GrantedAuthority::getAuthority) // extract role name
                         .collect(Collectors.toList());
        
        return roles;
        
    }
}