package com.rakshaashtankar.user_service.config;

import com.rakshaashtankar.user_service.model.Role;
import com.rakshaashtankar.user_service.model.User;
import com.rakshaashtankar.user_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer (UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if(userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin1");
            admin.setEmail("admin@xyz.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            admin.setPasswordChanged(false);
            userRepository.save(admin);
        }
    }
}
