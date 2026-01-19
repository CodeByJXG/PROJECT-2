package com.example.demo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.model.MyUser;
import com.example.demo.model.Librarian;
import com.example.demo.repo.MyRepository;
import com.example.demo.repo.MyLibrarianRepository;
import com.example.demo.security.JwtProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MyRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider provider;
    private final MyLibrarianRepository adminRepo;
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {

                if (repo.findByUsername("JulharieMaddin").isEmpty()) {
                MyUser admin = new MyUser();
                admin.setUsername("JulharieMaddin");
                admin.setPassword(passwordEncoder.encode("JulBackendDev"));
                admin.setRoles(List.of("ROLE_MODERATOR"));
                Librarian librarian= new Librarian();
                librarian.setLibrarianUsername(admin.getUsername());
                librarian.setUser(repo.save(admin));
                adminRepo.save(librarian);
                
                }
            
        };
    }
}