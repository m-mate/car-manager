package com.example.car_manager.repo;

import com.example.car_manager.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CarRepository extends JpaRepository<Car, Integer> {
    Car findByVin(String vin);





    List<Car> findByUserUsername(String username);
}
