package com.example.car_manager.repo;

import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarDataTmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarDataTmpRepository extends JpaRepository<CarDataTmp, Long> {
    List<CarDataTmp> findByCar_Vin(String vin);
    void deleteAllByCar_Vin(String vin);
// Custom delete method
    @Query("SELECT c FROM CarDataTmp c WHERE c.car.vin = :vin ORDER BY c.timeStamp DESC LIMIT 1")
    CarDataTmp findLatestByCarVin(@Param("vin") String vin);


    void deleteAllByCar(Car car);

}
