package com.example.car_manager.repo;

import com.example.car_manager.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarRepository extends JpaRepository<Car, Integer> {
}
