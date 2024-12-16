package com.example.car_manager.service;

import com.example.car_manager.model.CarData;
import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.repo.CarDataRepository;
import com.example.car_manager.repo.CarDataTmpRepository;
import com.example.car_manager.repo.CarRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarDataTmpService {
    private static final Logger log = LoggerFactory.getLogger(CarDataTmpService.class);
    CarDataTmpRepository carDataTmpRepository;

    CarDataRepository carDataRepository;
    private CarRepository carRepository;

    @Autowired
    public void setCarDataRepository(CarDataRepository carDataRepository) {
        this.carDataRepository = carDataRepository;
    }

    @Autowired
    public void setCarDataTmpRepository(CarDataTmpRepository carDataTmpRepository) {
        this.carDataTmpRepository = carDataTmpRepository;
    }

    public CarDataTmp getCarDataTmpById(Long id) {
        return carDataTmpRepository.findById(id).get();
    }

    @Transactional
    public CarDataTmp saveCarDataTmp(CarDataTmp carDataTmp) {
        // Logger to log errors
        //Logger logger = LoggerFactory.getLogger(CarDataTmpService.class);


        try {
            // Fetch all temporary data for the same car (VIN)


            //log.info("Save carDataTmp"+ carDataTmp.getCar().toString());
            carDataTmpRepository.save(carDataTmp);
            List<CarDataTmp> carDataTmpList = carDataTmpRepository.findByCar_Vin(carDataTmp.getCar().getVin());
            //log.info("carDataTmpList: {}", carDataTmpList);
            log.info("carDataTmpSize: {}", carDataTmpList.size() );
            if (carDataTmpList.size() >= 60) {
                // Calculate the averages
                double avgSpeed = carDataTmpList.stream().mapToDouble(CarDataTmp::getSpeed).average().orElse(0.0);
                log.info("avgSpeed: {}", avgSpeed);
                int avgRpm = (int) carDataTmpList.stream().mapToInt(CarDataTmp::getRpm).average().orElse(0);
                log.info("avgRpm: {}", avgRpm);
                double avgFuelLevel = carDataTmpList.stream().mapToDouble(CarDataTmp::getFuelLevel).average().orElse(0.0);
                log.info("avgFuelLevel: {}", avgFuelLevel);
                double avgFuelRate = carDataTmpList.stream().mapToDouble(CarDataTmp::getFuelRate).average().orElse(0.0);
                log.info("avgFuelRate: {}", avgFuelRate);

                CarData carData = new CarData();
                carData.setSpeed(avgSpeed);
                carData.setRpm(avgRpm);
                carData.setFuelLevel(avgFuelLevel);
                carData.setFuelRate(avgFuelRate);
                carData.setTimeStamp(carDataTmp.getTimeStamp()); // Use the latest timestamp
                carData.setCar(carDataTmp.getCar());




                carDataTmpRepository.deleteAllByCar_Vin(carDataTmp.getCar().getVin());

                carDataRepository.save(carData);
            }

            return carDataTmp;

        } catch (DataAccessException e) {
            log.error("Database error while saving car data: {}", e.getMessage(), e);
            throw new RuntimeException("Database error occurred", e);
        } catch (NullPointerException e) {
            log.error("Null pointer error while processing car data: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Received null data, please check the inputs.", e);
        } catch (Exception e) {
            //logger.error("Unexpected error while processing car data: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    public CarDataTmp getLatestCarDataTmp(String vin) {
        return carDataTmpRepository.findLatestByCarVin(vin);
    }

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
