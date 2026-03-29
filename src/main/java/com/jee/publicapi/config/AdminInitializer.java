package com.jee.publicapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jee.publicapi.entity.User;
import com.jee.publicapi.repository.UserRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository repo,
                                  PasswordEncoder encoder) {

        return args -> {

            if (repo.findByEmail("admin@jee.com").isEmpty()) {

                User admin = new User();
                admin.setEmail("admin@jee.com");
                admin.setPassword(encoder.encode("Admin@123"));

                // ✅ Store plain role here
                admin.setRole("ADMIN");

                admin.setEnabled(true);
                admin.setVerified(true);

                // 🔥 REQUIRED FIELDS
                admin.setAadharCard("000000000000");
                admin.setFirstName("System");
                admin.setLastName("Admin");

                repo.save(admin);

            }
        };
    }
}