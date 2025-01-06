package com.example.car_manager.controller;

import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
public class CarController {

    CarService carService;

    @Autowired
    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/save")
    public String add(@RequestBody Car car) {
        try {
            carService.save(car);
            return "Car added successfully!";
        } catch (Exception e) {

            return "Error occurred while adding Car: " + e.getMessage();
        }
    }
}
