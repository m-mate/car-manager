package com.example.car_manager.controller;

import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.service.CarDataTmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarDataTmpController {

    private CarDataTmpService carDataTmpService;

    @Autowired
    public void setCarDataTmpService(CarDataTmpService carDataTmpService) {
        this.carDataTmpService = carDataTmpService;
    }

    @PostMapping("/add")
    public String add(@RequestBody CarDataTmp carDataTmp) {
        try {

            carDataTmpService.saveCarDataTmp(carDataTmp);
            return "CarDataTmp added successfully!";
        } catch (Exception e) {

            return "Error occurred while adding CarDataTmp: " + e.getMessage();
        }
    }

}
