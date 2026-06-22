package com.vaultkeeper.vaultKeeper.config;

import com.vaultkeeper.vaultKeeper.model.User;
import com.vaultkeeper.vaultKeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${vault.default.username}")
    private String defaultUsername;

    @Value("${vault.default.password}")
    private String defaultPassword;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername(defaultUsername)) {
            User user = new User();
            user.setUsername(defaultUsername);
            user.setPasswordHash(passwordEncoder.encode(defaultPassword));
            userRepository.save(user);
            System.out.println("Default user created: " + defaultUsername);
        } else {
            System.out.println("Default user already exists: " + defaultUsername);
        }
    }
}