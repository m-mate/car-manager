package com.example.car_manager.service;

import com.example.car_manager.model.Car;
import com.example.car_manager.repo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CarService {
    CarRepository carRepository;

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car save(Car car) {
        if (carRepository.existsByVin(car.getVin())) {
            return carRepository.findByVin(car.getVin());
        }
        return carRepository.save(car);
    }

    public Car updateCar(Integer id, Car carDetails) {
        Optional<Car> carOptional = carRepository.findById(id);

        if (carOptional.isPresent()) {
            Car existingCar = carOptional.get();

            if (carDetails.getType() != null) {
                existingCar.setType(carDetails.getType());
            }


            return carRepository.save(existingCar);
        } else {
            throw new RuntimeException("Car not found with ID: " + id);
        }
    }

    public void deleteCar(Integer id) {
        carRepository.deleteById(id);
    }

    public Car findByVin(String vin){
        return carRepository.findByVin(vin);
    }

    public Car findById(Integer id) {
        return carRepository.findById(id).get();
    }

}
