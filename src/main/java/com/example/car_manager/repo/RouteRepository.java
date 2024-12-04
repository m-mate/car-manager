package com.example.car_manager.repo;

import com.example.car_manager.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RouteRepository extends JpaRepository<Route, Long> {
}
