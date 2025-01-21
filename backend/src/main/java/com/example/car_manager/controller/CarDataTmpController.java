package com.example.car_manager.controller;

import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.repo.CarRepository;
import com.example.car_manager.service.CarDataTmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("get/{vin}")
    public ResponseEntity<CarDataTmp> getCarDataTmp(@PathVariable String vin) {
        CarDataTmp currentData = carDataTmpService.getLatestCarDataTmp(vin);
        if (currentData != null) {
            return ResponseEntity.ok(currentData); // Return 200 OK with data
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if no data found
        }
    }

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
