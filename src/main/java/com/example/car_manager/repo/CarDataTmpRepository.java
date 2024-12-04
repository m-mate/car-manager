package com.example.car_manager.repo;

import com.example.car_manager.model.CarDataTmp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarDataTmpRepository extends JpaRepository<CarDataTmp, Long> {
    List<CarDataTmp> findByCar_Vin(String vin);

}
