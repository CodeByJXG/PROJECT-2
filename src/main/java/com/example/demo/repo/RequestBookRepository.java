package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RequestBook;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RequestBookRepository extends JpaRepository<RequestBook,Integer>{
    @Query("SELECT rq FROM RequestBook rq WHERE rq.librarianUsername = :librarianUsername ")
    List<RequestBook> findByLibrarianUsername(@Param("librarianUsername") String librarianUsername);
    List<RequestBook> findByUserId(int id); 
    @Query("SELECT r FROM RequestBook r WHERE r.book.id = :bookId" )
     List<RequestBook> findByBookId(@Param("bookId") int bookId);
    Long countByUserIdAndReqStatus(Long userId, String reqStatus);

    
}