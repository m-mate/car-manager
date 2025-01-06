package com.example.car_manager.service;

import com.example.car_manager.dto.UserDTO;
import com.example.car_manager.model.User;
import com.example.car_manager.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    private UserRepository userRepository;

    private JWTService jwtService;

    @Autowired
    public void setJwtService(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    private AuthenticationManager authenticationManager;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            user.setPassword(encoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        return null;

    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public User updateUser(String username, User user) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(encoder.encode(user.getPassword()));
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                existingUser.setFirstName(user.getFirstName());
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                existingUser.setLastName(user.getLastName());
            }

            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user-> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole())).collect(Collectors.toList());
    }

    public String verify(User user) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),
                        user.getPassword()));
        User currentUser = userRepository.findByUsername(user.getUsername());
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername(),currentUser.getRole());
        }
        return "Fail";
    }

    public User findByUsername(String username) {
        userRepository.findByUsername(username);
    }
}
