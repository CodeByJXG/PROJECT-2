package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.AcceptedRequest;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AcceptedRequestRepository extends JpaRepository<AcceptedRequest,Integer>{
     AcceptedRequest findByRequestBookId(int id);
     @Query("SELECT a FROM AcceptedRequest a WHERE a.book.id = :bookId" )
     List<AcceptedRequest> findByBookId(@Param("bookId") int bookId);
     @Query("SELECT a FROM AcceptedRequest a WHERE a.user.id = :userId" )
     List<AcceptedRequest> findByUserId(@Param("userId") int userId);
    Long countByUserId(Long id);
     Long countByLibrarianUsername(String librarianUsername);
     List<AcceptedRequest> findByLibrarianUsername(String librarianUsername);
}