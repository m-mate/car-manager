package com.example.car_manager.service;

import com.example.car_manager.model.Car;
import com.example.car_manager.repo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarService {
    CarRepository carRepository;

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car save(Car car) {
        car.setVin(generateRandomVin());
        return carRepository.save(car);
    }

    private String generateRandomVin() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
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

    public List<Car> findByUserName(String username) { return carRepository.findByUserUsername(username); }

}
