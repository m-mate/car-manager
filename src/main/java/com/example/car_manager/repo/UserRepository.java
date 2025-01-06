package com.example.car_manager.repo;

import com.example.car_manager.model.Role;
import com.example.car_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    User findFirstByRole(Role role);
}
