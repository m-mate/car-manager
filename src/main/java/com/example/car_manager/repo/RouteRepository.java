package com.example.car_manager.repo;

import com.example.car_manager.model.Car;
import com.example.car_manager.model.Route;
import com.example.car_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByUserAndCar(User user, Car car);
}
