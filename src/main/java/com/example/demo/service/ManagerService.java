	package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.LibrarianDashboardDto;
import com.example.demo.dto.BookCreateRequest;
import com.example.demo.model.Book;
import com.example.demo.model.Librarian;
import com.example.demo.dto.RequestBooksUserAdmin;
import com.example.demo.model.AcceptedRequest;
import com.example.demo.model.RequestBook;
import com.example.demo.repo.BookRepository;
import com.example.demo.repo.MyLibrarianRepository;
import com.example.demo.repo.RequestBookRepository;
import com.example.demo.repo.AcceptedRequestRepository;
import com.example.demo.security.MyUserDetails;
import java.io.File;
import com.example.demo.repo.MyRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.core.io.ByteArrayResource;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.BookCreateResponse;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ManagerService{
    private final MyRepository userRepo;
    private final MyLibrarianRepository repo;
    private final BookRepository bookRepo;
    private final BookDetails bookDetails;
    private final RequestBookRepository requestBookRepo;
    private final AcceptedRequestRepository acceptedRepo;
    private final String constDownloadDir = "download/pdfs/";
    
    
    
    public LibrarianDashboardDto getLibrarianDataStatus(Authentication auth){
        long totalUsers = userRepo.count();
        long totalLibrarians= repo.count();
        long totalBooks = bookRepo.count();
        Long booksApprove = Long.valueOf(0);
        try{
         booksApprove = acceptedRepo.countByLibrarianUsername(getLibrarianUsername(auth));
        }catch(Exception ex){
            //Do not touch
        }
        return new LibrarianDashboardDto()
        .setTotalUsers(totalUsers)
        .setTotalLibrarians(totalLibrarians)
        .setTotalBooks(totalBooks)
        .setBooksApprove(booksApprove);
    }
    
    
    public void setLibrarianUsername(Authentication auth, String username){
        Object principal = auth.getPrincipal();
        if(principal instanceof MyUserDetails){
            int id = ((MyUserDetails) principal).getId();
            Librarian librarian= repo.findByUserId(id);
            if(librarian==null){
                throw new RuntimeException("Cannot find librarian by id provided");
                
            }
            try{
            String backLibrarianUsername = librarian.getLibrarianUsername();
            librarian.setLibrarianUsername(username);
            if(backLibrarianUsername!=null){
            List<Book> books = bookRepo.findByLibrarianUsername(backLibrarianUsername);
            List<AcceptedRequest> accepted = acceptedRepo.findByLibrarianUsername(backLibrarianUsername);
            List<RequestBook> rq = requestBookRepo.findByLibrarianUsername(backLibrarianUsername);
            for(Book b : books){
                b.setLibrarianUsername(username);
                bookRepo.save(b);
            }
             for(AcceptedRequest a : accepted){
                a.setLibrarianUsername(username);
                acceptedRepo.save(a);
            }
             for(RequestBook r : rq){
                r.setLibrarianUsername(username);
                requestBookRepo.save(r);
            }
            }
            repo.save(librarian);
            }catch(Exception ex){
                throw new RuntimeException("Username is not available");
            }
        }else{
        throw new RuntimeException("Authorization error");
        }
    }
    
    
   
    //Done ✅. 1st Pending
    public List<BookCreateResponse> getAllBooks(String username){
        Librarian librarian = repo.findByUserUsername(username);
        List<Book> allBooks = librarian.getBooks();
        return allBooks.stream().map(book->bookDetails.getBook(book)).toList();
         
        } 
    
    //Done ✅
    public BookCreateResponse saveBook(String username, 
    BookCreateRequest bookCreateRequest,MultipartFile pdfFile)throws Exception{
        
        Librarian librarian = repo.findByUserUsername(username);
        Book book = new Book();
        
        book.setTitle(bookCreateRequest.getTitle());
        book.setAuthor(bookCreateRequest.getAuthor());
        book.setLibrarianUsername(librarian.getLibrarianUsername());
        book.setStock(bookCreateRequest.getStock());
        book.setData(pdfFile.getBytes());
        book.setLibrarian(librarian);
        bookRepo.save(book);
         return bookDetails.getBook(book);
    }
    
    //Done ✅ 
    public BookCreateResponse updateBook(String username, 
    BookCreateRequest bookCreateRequest,MultipartFile pdfFile,int id)throws Exception{
        Librarian librarian = repo.findByUserUsername(username);
        Book book =  bookRepo.findById(id).get();
        book.setTitle(bookCreateRequest.getTitle());
        book.setAuthor(bookCreateRequest.getAuthor());
        book.setLibrarianUsername(librarian.getLibrarianUsername());
        book.setStock(bookCreateRequest.getStock());
        if(pdfFile != null){
            //Todo
            book.setData(pdfFile.getBytes());
        }
        bookRepo.save(book);
         return bookDetails.getBook(book);
    }


    
    //Done ✅ 
    @Transactional
    public void deleteBooks(String username, int id) {
    Librarian librarian = repo.findByUserUsername(username);
    if (librarian == null) {
        throw new RuntimeException("Librarian not found");
    }
    
    Book book = bookRepo.findById(id).orElse(null);
    if (book == null) {
        throw new RuntimeException("Book not found");
    }
    
    // Check authorization
    if (!librarian.getLibrarianUsername().equals(book.getLibrarianUsername())) {
        throw new RuntimeException("Unauthorized: You can only delete your own books");
    }
    
    List<AcceptedRequest> allAcceptedRequestBooks = acceptedRepo.findByBookId(id);
    List<RequestBook> allRequestBooks = requestBookRepo.findByBookId(id);
    
    
    acceptedRepo.deleteAll(allAcceptedRequestBooks);
    requestBookRepo.deleteAll(allRequestBooks);
    
    // Delete from database
    bookRepo.delete(book);
    System.out.println("Book deleted successfully: " + book.getTitle());
}
    
    
    
    
    
    
    //Done ✅ 
    public AcceptedRequest setRequestAccept(int id, Authentication auth)throws Exception {
        String librarianUsername= getLibrarianUsername(auth);
        RequestBook rqBook = requestBookRepo.findById(id).get();
        if(rqBook==null){
            throw new RuntimeException("Cannot find requestBook");
        }
        if(!rqBook.getReqStatus().equals(Status.PENDING.name())){
            throw new RuntimeException("requestedBook is already configured");
        }
        if(!rqBook.getLibrarianUsername().equals(librarianUsername)){
            throw new RuntimeException("requestedBook is not yours");
        }
        int downloadAttemp = 5;
        String username = rqBook.getUser().getUsername();
        String reQlibrarianUsername = rqBook.getBook().getLibrarianUsername();
        AcceptedRequest acceptedRequest = new AcceptedRequest();
        acceptedRequest.setUser(rqBook.getUser());
        acceptedRequest.setBook(rqBook.getBook());
        acceptedRequest.setLibrarianUsername(reQlibrarianUsername);
        acceptedRequest.setRequestBookId(rqBook.getId());
        acceptedRequest.setDownloadAttemp(downloadAttemp);
        Book book = rqBook.getBook();
        if(book.getStock()<=0){
            throw new RuntimeException("Insufficient stock");
        }
        book.setStock(book.getStock()-1);
        Book saveBook = bookRepo.save(book);
        rqBook.setBook(saveBook);
        acceptedRequest.setBook(rqBook.getBook());
        rqBook.setReqStatus(Status.ACCEPTED.name());
        requestBookRepo.save(rqBook);
        return acceptedRepo.save(acceptedRequest);
    }
    
    //Done ✅ 
    public void setRequestDenied(int id,Authentication auth){
        String librarianUsername= getLibrarianUsername(auth);
        RequestBook rqBook = requestBookRepo.findById(id).get();
        if(rqBook==null){
            throw new RuntimeException("Cannot find requestBook");
        }
        
                if(!rqBook.getReqStatus().equals(Status.PENDING.name())){
            throw new RuntimeException("requestedBook is already configured");
        }
        if(!rqBook.getLibrarianUsername().equals(librarianUsername)){
            throw new RuntimeException("requestedBook is not yours");
        }
        rqBook.setReqStatus(Status.DENIED.name());
        requestBookRepo.save(rqBook);
    }
    
    
    
    //Done ✅ 
    public void deleteRequestBook(int id, Authentication auth){
        String librarianUsername= getLibrarianUsername(auth);
        RequestBook rqBook = requestBookRepo.findById(id).get();
        if(rqBook==null){
            throw new RuntimeException("Cannot find requestBook");
        }
        if(!rqBook.getLibrarianUsername().equals(librarianUsername)){
            throw new RuntimeException("requestedBook is not yours");
        }
        AcceptedRequest req = acceptedRepo.findByRequestBookId(id);
        
        if(req!=null){
            acceptedRepo.delete(req);
            Book book = rqBook.getBook();
            book.setStock(book.getStock()+1);
            bookRepo.save(book);
            rqBook.setBook(book);
        }
        requestBookRepo.delete(rqBook);
    }
    
    
    
    //Done ✅ 
    public List<RequestBooksUserAdmin> getAllRequestBook(Authentication auth){
        String librarianUsername= getLibrarianUsername(auth);
        List<RequestBook> rqBook = requestBookRepo.findByLibrarianUsername(librarianUsername);
        if(rqBook==null){
            return List.of();
        }
        List<RequestBooksUserAdmin> convertedBooks  =  rqBook.stream()
        .map(b -> new RequestBooksUserAdmin()
        .setRequestBookId(b.getId())
        .setUserUsername(b.getUser().getUsername())
        .setBookTitle(b.getBook().getTitle())
        .setBookAuthor(b.getBook().getAuthor())
        .setBookStatus(b.getReqStatus())
        ).toList ();
        return convertedBooks;
    }
     
    
    
    
    
    public String getLibrarianUsername(Authentication auth){
        if(auth.getPrincipal() instanceof MyUserDetails){
        int id = ((MyUserDetails) auth.getPrincipal()).getId();
        Librarian librarian= repo.findByUserId(id);
        return librarian.getLibrarianUsername();
        }
        throw new RuntimeException("Something went wrong!");
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