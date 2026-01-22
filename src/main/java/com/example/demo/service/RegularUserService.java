package com.example.demo.service;
import com.example.demo.dto.UserDashboardDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import com.example.demo.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.demo.security.MyUserDetails;
import com.example.demo.dto.PublicBookResponseImpl.PublicUserBookResponse;
import com.example.demo.dto.RequestBookDto;
import com.example.demo.repo.MyLibrarianRepository;
import com.example.demo.repo.MyRepository;
import com.example.demo.repo.RequestBookRepository;
import java.util.stream.Collectors;
import com.example.demo.model.Librarian;
import com.example.demo.model.Book;
import com.example.demo.model.RequestBook;
import com.example.demo.model.MyUser;
import com.example.demo.model.AcceptedRequest;
import com.example.demo.dto.UserRequestBookDto;
import com.example.demo.dto.UserRequestBookDtoImpl.RequestAcceptedDto;
import com.example.demo.dto.UserRequestBookDtoImpl.RequestDecLPendDto;
import com.example.demo.repo.AcceptedRequestRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.example.demo.repo.BookRepository;
import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import org.springframework.core.io.ByteArrayResource;

@Service
@RequiredArgsConstructor
public class RegularUserService{
    
    private final MyLibrarianRepository librarianRepo;
    private final MyRepository userRepo;
    private final RequestBookRepository requestBookRepo;
    private final BookRepository bookRepo;
    private final AcceptedRequestRepository acceptedRepo;
    
    // Done ✅✓
    public PublicUserBookResponse setRequest(RequestBookDto dto,int bookId, Authentication auth) throws Exception{
        Librarian librarian = librarianRepo.findByLibrarianUsername(dto.getLibrarianUsername());
        if(librarian==null){
            throw new RuntimeException("Cannot find Librarian by "+ dto.getLibrarianUsername());
        }
        List<Book> listOfBooks = librarian.getBooks();
            if(auth.getPrincipal() instanceof MyUserDetails){
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            MyUser user = userRepo.findById(userDetails.getId()).get();
            Book book = getBook(listOfBooks,bookId);
            if(user==null){
                throw new RuntimeException("Cannot find user");
            }
                    if(validateRequestBook(librarian,book,user)){
            throw new RuntimeException("Book is already requested");
        }
            RequestBook requestBook = new RequestBook();
            requestBook.setUser(user);
            requestBook.setBook(book);
            requestBook.setLibrarianUsername(librarian.getLibrarianUsername());
            requestBook.setReqStatus(Status.PENDING.name());
            requestBookRepo.save(requestBook);
            return new PublicUserBookResponse()
        .setId(book.getId())
        .setTitle(book.getTitle())
        .setAuthor(book.getAuthor())
        .setStock(book.getStock())
        .setLibrarianUsername(book.getLibrarianUsername())
        .setRequestStatus(requestBook.getReqStatus());
           }else{
                throw new RuntimeException("User is not instance of UserDetails");
            }
    }
    
    // Need debugging to add new features Done ✅ 
    public List<? extends UserRequestBookDto> getRequest(Authentication auth){
        Object principal = auth.getPrincipal();
        List<UserRequestBookDto> allRequestBooks;
        
        if( principal instanceof MyUserDetails){
             int userId = ((MyUserDetails) principal).getId();
             MyUser loadedUser = userRepo.findById(userId).get();
             if(loadedUser==null){
                 throw new RuntimeException("Cannot find user");
             }
       List<RequestBook> requestBooks = requestBookRepo.findByUserId(userId);
       if(requestBooks==null || requestBooks.isEmpty()){
           return List.of();
       }
       allRequestBooks = requestBooks.stream()
    .map(r -> {
        if (Status.ACCEPTED.name().equals(r.getReqStatus())) {
            AcceptedRequest acc = acceptedRepo.findByRequestBookId(r.getId());

            return new RequestAcceptedDto()
                    .setRequestId(r.getId())
                    .setTitle(r.getBook().getTitle())
                    .setAuthor(r.getBook().getAuthor())
                    .setStock(r.getBook().getStock())
                    .setLibrarianUsername(r.getBook().getLibrarianUsername())
                    .setRequestStatus(r.getReqStatus())
                    //This is the debugging progress
                    .setAcceptReqId(acc.getId())
                    .setDownloadAttempLeft(acc.getDownloadAttemp());
        } else {
            return new RequestDecLPendDto()
                    .setRequestId(r.getId())
                    .setTitle(r.getBook().getTitle())
                    .setAuthor(r.getBook().getAuthor())
                    .setStock(r.getBook().getStock())
                    .setLibrarianUsername(r.getBook().getLibrarianUsername())
                    .setRequestStatus(r.getReqStatus());
        }
    })
    .collect(Collectors.toList());
       
       return allRequestBooks;
             }else{
            throw new RuntimeException("Unknown Authentication");
        }
    }
    
    
    
    private boolean validateRequestBook(Librarian librarian, Book book , MyUser user){
        List<RequestBook> listOfBooks = requestBookRepo.findByLibrarianUsername(librarian.getLibrarianUsername());
        if(listOfBooks==null) return false;
        for(RequestBook b : listOfBooks){
            if(b.getBook().getId()==book.getId()){
                if(b.getUser().getId()==user.getId()){
                    return true;
                }
            }
        }
        return false;
    }
    
    // NEED DEBUGGING Done ✅ 
    @Transactional
    public void deleteRequest(int id, Authentication auth){
        String userUsername= getUserUsername(auth);
        RequestBook rqBook = requestBookRepo.findById(id).get();
        if(rqBook.getReqStatus().equals(Status.ACCEPTED.name())){
            throw new RuntimeException("You cannot delete a request book if accepted!");
        }
        if(rqBook==null){
            throw new RuntimeException("Cannot find requestBook");
        }
        if(!rqBook.getUser().getUsername().equals(userUsername)){
            throw new RuntimeException("requestedBook is not yours");
        }
        AcceptedRequest req = acceptedRepo.findByRequestBookId(id);
        if(req!=null){
        acceptedRepo.delete(req);
        }
        requestBookRepo.delete(rqBook);
    }
    
    //Done ✅ 
        @Transactional
public void returnRequest(int id, Authentication auth){
    String userUsername = getUserUsername(auth);

    // Load request book safely
    RequestBook rqBook = requestBookRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Cannot find requestBook"));

    if(!rqBook.getReqStatus().equals(Status.ACCEPTED.name())){
        throw new RuntimeException("The book is not accepted; you cannot return it, just delete it.");
    }

    if(!rqBook.getUser().getUsername().equals(userUsername)){
        throw new RuntimeException("This request does not belong to you.");
    }

    // Delete AcceptedRequest
    AcceptedRequest req = acceptedRepo.findByRequestBookId(id);
    if(req != null){
        acceptedRepo.delete(req);
    }

    // Update stock
    Book book = rqBook.getBook();
    book.setStock(book.getStock() + 1);
    bookRepo.save(book);

    // Delete the requestBook itself
    requestBookRepo.delete(rqBook);
}
    
    //✅ 
    public ByteArrayResource getDownloadFile(int id, Authentication auth,String librarianUsername) throws MalformedURLException{
        AcceptedRequest acceptBook = acceptedRepo.findById(id).get();
        if(acceptBook == null){
            throw new RuntimeException("File is not accepted");
        }
        String acceptUsername = acceptBook.getUser().getUsername();
        if(!acceptUsername.equals(getUserUsername(auth))){
            throw new RuntimeException("Accept book is not yours");
        }
        if(!acceptBook.getLibrarianUsername().equals(librarianUsername)){
            throw new RuntimeException("Request LibrarianUsername is not equals");
        }
        if(acceptBook.getDownloadAttemp()<=0){
            throw new RuntimeException("Maximum downloads reach!");
        }
        
        acceptBook.setDownloadAttemp(acceptBook.getDownloadAttemp()-1);
        
        acceptedRepo.save(acceptBook);
        
        return new ByteArrayResource(acceptBook.getBook().getData());
    }
    
    
    public String getUserUsername(Authentication auth){
        if(auth.getPrincipal() instanceof MyUserDetails){
        int id = ((MyUserDetails) auth.getPrincipal()).getId();
        MyUser user= userRepo.findById(id).get();
        return user.getUsername();
        }
        throw new RuntimeException("Something went wrong!");
    }
    
    public int getUserId(Authentication auth){
        if(auth.getPrincipal() instanceof MyUserDetails){
         int id = ((MyUserDetails) auth.getPrincipal()).getId();
         return id;
        }
        throw new RuntimeException("Something went wrong!");
        }
        
    


public UserDashboardDto getUsersDataStatus(Authentication auth){
    long allBooks = bookRepo.count();
    Long availableBooks= bookRepo.countByStockGreaterThan(0);
    Long id = (long) getUserId(auth);
    Long myBorrowedBooks =  acceptedRepo.countByUserId(id);
    Long myPendingRequest= requestBookRepo.countByUserIdAndReqStatus(id,Status.PENDING.name());
    return new UserDashboardDto()
    .setTotalBooks(allBooks)
    .setAvailableBooks(availableBooks)
    .setMyBorrowedBooks(myBorrowedBooks)
    .setMyPendingRequest(myPendingRequest);
}
    
    private Book getBook(List<Book> listOfBooks,int bookId){
        for(Book book: listOfBooks){
            if(bookId==book.getId()){
                return book;
            }
        }
        
                    throw new RuntimeException("Cannot find bookId "+bookId);
    }
}