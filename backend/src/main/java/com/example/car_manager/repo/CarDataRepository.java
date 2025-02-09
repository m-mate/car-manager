package com.example.car_manager.repo;

import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarData;
import com.example.car_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CarDataRepository extends JpaRepository<CarData, Long> {


    List<CarData> findByCar(Car car);

    List<CarData> findByCarAndInRouteFalseOrderByTimeStampAsc(Car car);

    List<CarData> findByCarAndTimeStampBetween(Car car, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    void deleteByCarAndTimeStampBetween(Car car, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
