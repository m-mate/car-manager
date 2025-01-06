package com.example.car_manager.config;

import com.example.car_manager.model.Role;
import com.example.car_manager.model.User;
import com.example.car_manager.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);



    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;



    public AdminInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public void run(String... args) {
        User existingAdmin = userRepository.findFirstByRole(Role.ROLE_ADMIN);
        if (existingAdmin == null) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
            System.out.println("Admin user created: " + adminUsername);
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
