package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.model.MyUser;
import com.example.demo.model.Librarian;
import com.example.demo.repo.MyRepository;
import com.example.demo.repo.MyLibrarianRepository;
import com.example.demo.security.JwtProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.AdminAccountCreator;

@Service
@RequiredArgsConstructor
public class AccountCreator{
    private final MyRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider provider;
    private final MyLibrarianRepository adminRepo;
    
    public void create(AdminAccountCreator account){
        MyUser admin = new MyUser();
          if (repo.findByUsername(account.getUsername()).isEmpty()) {
                admin.setUsername(account.getUsername());
                admin.setPassword(passwordEncoder.encode(account.getPassword()));
                }
        if(account.getRoles().equals("ADMIN")&&admin.getUsername()!=null){
                admin.setRoles(List.of("ROLE_ADMIN"));
                Librarian librarian= new Librarian();
                librarian.setLibrarianUsername(admin.getUsername());
                librarian.setUser(repo.save(admin));
                adminRepo.save(librarian);
     }else{
        admin.setRoles(List.of("ROLE_USER"));
        repo.save(admin);
    }
    
    
}

}