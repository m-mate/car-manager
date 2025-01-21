package com.example.car_manager.dto;

import com.example.car_manager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;

    public UserDTO(Long id, String username, String email, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}