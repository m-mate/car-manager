package com.example.car_manager.controller;

import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.repo.CarRepository;
import com.example.car_manager.service.CarDataTmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarDataTmpController {

    private CarDataTmpService carDataTmpService;
    private CarRepository carRepository;

    @Autowired
    public void setCarDataTmpService(CarDataTmpService carDataTmpService) {
        this.carDataTmpService = carDataTmpService;
    }

    @PostMapping("/add/{vin}")
    public String add(@RequestBody CarDataTmp carDataTmp, @PathVariable String vin) {
        try {
            carDataTmp.setCar(carRepository.findByVin(vin));
            carDataTmpService.saveCarDataTmp(carDataTmp);
            return "CarDataTmp added successfully!";
        } catch (Exception e) {

            return "Error occurred while adding CarDataTmp: " + e.getMessage();
        }
    }

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
