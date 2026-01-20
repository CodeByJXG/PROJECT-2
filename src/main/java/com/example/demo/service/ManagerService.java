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
    private String uploadDir = "uploads/pdfs/";
    private final String backupDir=uploadDir;
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
    
    
    
    public String saveFile(MultipartFile file) throws IOException {
       if (file == null || file.isEmpty()) return null;

    // Ensure upload directory exists
    Path uploadPath = Paths.get(uploadDir); // uploadDir must be set like "/uploads/librarian1"
    if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
    }

    // Generate a unique filename
    String originalFileName = file.getOriginalFilename();
    String ext = "";
    if (originalFileName != null && originalFileName.contains(".")) {
        ext = originalFileName.substring(originalFileName.lastIndexOf("."));
    }
    String uniqueFileName = UUID.randomUUID().toString() + ext;

    // Resolve full path
    Path filePath = uploadPath.resolve(uniqueFileName);

    // Copy file
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    // Return just the filename (store in DB)
    return uniqueFileName;
    }
    
    
    
    public List<BookCreateResponse> getAllBooks(String username){
        Librarian librarian = repo.findByUserUsername(username);
        List<Book> allBooks = librarian.getBooks();
        return allBooks.stream().map(book->bookDetails.getBook(book)).toList();
         
        } 
    
    
    public BookCreateResponse saveBook(String username, 
    BookCreateRequest bookCreateRequest,MultipartFile pdfFile)throws Exception{
        
        Librarian librarian = repo.findByUserUsername(username);
        uploadDir = uploadDir+librarian.getLibrarianUsername();
        String filePath=saveFile(pdfFile);
        Book book =bookRepo.save( new Book()
        .setTitle(bookCreateRequest.getTitle())
        .setAuthor(bookCreateRequest.getAuthor())
        .setLibrarianUsername(librarian.getLibrarianUsername())
        .setStock(bookCreateRequest.getStock())
        .setFilePath(filePath)
        .setLibrarian(librarian));
        uploadDir = backupDir;
         return bookDetails.getBook(book);
    }
    
    
    public BookCreateResponse updateBook(String username, 
    BookCreateRequest bookCreateRequest,MultipartFile pdfFile,int id)throws Exception{
        Librarian librarian = repo.findByUserUsername(username);
        uploadDir = uploadDir+librarian.getLibrarianUsername();
        String filePath= null;
        if(pdfFile != null){
        filePath=saveFile(pdfFile);
        }
        Book book =  bookRepo.findById(id).get();
        book.setTitle(bookCreateRequest.getTitle());
        book.setAuthor(bookCreateRequest.getAuthor());
        book.setLibrarianUsername(librarian.getLibrarianUsername());
        book.setStock(bookCreateRequest.getStock());
        
        if(filePath!=null){
            String previousFilePath = book.getFilePath();
            deleteFile(backupDir+librarian.getLibrarianUsername()+"/"+previousFilePath);
            book.setFilePath(filePath);
            
        }
        bookRepo.save(book);
        uploadDir = backupDir;
         return bookDetails.getBook(book);
    }

    private void deleteFile(String filePath) {
    try {
        Path path = Paths.get(filePath);
        
        if (Files.exists(path)) {
            boolean result = Files.deleteIfExists(path);
            
            if (result) {
                System.out.println("File deleted successfully: " + filePath);
            } else {
                System.out.println("Failed to delete file: " + filePath);
            }
        } else {
            System.out.println("File does not exist: " + filePath);
        }
    } catch (IOException e) {
        System.err.println("Error occurred while deleting file: " + e.getMessage());
        e.printStackTrace();
    }
}
    
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
    
    for(AcceptedRequest a : allAcceptedRequestBooks){
        String acceptedFilePath = a.getFilePath();
        deleteFile(acceptedFilePath);
        File myFolder = new File(constDownloadDir+a.getUser().getUsername());
        myFolder.delete();
        }
    
    
    acceptedRepo.deleteAll(allAcceptedRequestBooks);
    requestBookRepo.deleteAll(allRequestBooks);
    
    // Delete physical file if it exists
    if (book.getFilePath() != null && !book.getFilePath().isEmpty()) {
        String fullPath = uploadDir + librarian.getLibrarianUsername() + "/" + book.getFilePath();
        File myLibrarianFolder = new File(uploadDir + librarian.getLibrarianUsername());
        deleteFile(fullPath);
        myLibrarianFolder.delete();
    }
    
    // Delete from database
    bookRepo.delete(book);
    System.out.println("Book deleted successfully: " + book.getTitle());
}
    
    
    
    
    
    
    
    
    //username is the Users Username || miss consumption bookName is equals to BookPath in Book
    public String saveDownloadFile(String username,String bookName,String librarianUsername) throws IOException {
        String downloadDir = constDownloadDir+username;
    // Ensure upload directory exists
    Path downloadPath = Paths.get(downloadDir); // uploadDir must be set like "/uploads/librarian1"
    if (!Files.exists(downloadPath)) {
        Files.createDirectories(downloadPath);
    }
    Path originalPath = Paths.get(uploadDir+librarianUsername+"/"+bookName);
    if (!Files.exists(originalPath)) {
        throw new RuntimeException("Cannot find file");
    }
    
    // Generate a unique filename
    String ext = "";
    if (bookName != null && bookName.contains(".")) {
        ext = bookName.substring(bookName.lastIndexOf("."));
    }
    String uniqueFileName = UUID.randomUUID().toString() + ext;

    // Resolve full path
    Path filePath = downloadPath.resolve(uniqueFileName);

    // Copy file
    Files.copy(originalPath, filePath, StandardCopyOption.REPLACE_EXISTING);

    // Return just the filename (store in DB)
    return filePath.toString();
    }
    
    
    
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
        String bookName = rqBook.getBook().getFilePath();
        String reQlibrarianUsername = rqBook.getBook().getLibrarianUsername();
        String filePath = saveDownloadFile(username,bookName,librarianUsername);
        AcceptedRequest acceptedRequest = new AcceptedRequest();
        acceptedRequest.setFilePath(filePath);
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
        Librarian librarian= repo.findById(id).get();
        return librarian.getLibrarianUsername();
        }
        throw new RuntimeException("Something went wrong!");
    }
    
    
    
    
    
    
    
    
    
    }