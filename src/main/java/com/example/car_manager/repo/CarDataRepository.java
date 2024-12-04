package com.example.car_manager.repo;

import com.example.car_manager.model.CarData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarDataRepository extends JpaRepository<CarData, Long> {
}
