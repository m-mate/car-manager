package com.example.car_manager.service;

import com.example.car_manager.model.CarData;
import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.repo.CarDataRepository;
import com.example.car_manager.repo.CarDataTmpRepository;
import com.example.car_manager.repo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarDataTmpService {
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

    public CarDataTmp saveCarDataTmp(CarDataTmp carDataTmp) {
        // Logger to log errors
        //Logger logger = LoggerFactory.getLogger(CarDataTmpService.class);

        try {
            // Fetch all temporary data for the same car (VIN)
            carDataTmp.setCar(carRepository.findByVin(carDataTmp.getCar().getVin()));
            carDataTmpRepository.save(carDataTmp);
            List<CarDataTmp> carDataTmpList = carDataTmpRepository.findByCar_Vin(carDataTmp.getCar().getVin());

            // If there are 60 entries, calculate the average and move the data to CarData
            if (carDataTmpList.size() >= 60) {
                // Calculate the averages
                double avgSpeed = carDataTmpList.stream().mapToDouble(CarDataTmp::getSpeed).average().orElse(0.0);
                int avgRpm = (int) carDataTmpList.stream().mapToInt(CarDataTmp::getRpm).average().orElse(0);
                double avgFuelLevel = carDataTmpList.stream().mapToDouble(CarDataTmp::getFuelLevel).average().orElse(0.0);
                double avgFuelRate = carDataTmpList.stream().mapToDouble(CarDataTmp::getFuelRate).average().orElse(0.0);

                // Create a new CarData entry
                CarData carData = new CarData();
                carData.setSpeed(avgSpeed);
                carData.setRpm(avgRpm);
                carData.setFuelLevel(avgFuelLevel);
                carData.setFuelRate(avgFuelRate);
                carData.setTimeStamp(carDataTmp.getTimeStamp()); // Use the latest timestamp
                carData.setCar(carDataTmp.getCar());

                // Save the averaged CarData entry
                carDataRepository.save(carData);

                // Delete the processed entries from CarDataTmp
                carDataTmpRepository.deleteAll(carDataTmpList);
            }

            // Return the original carDataTmp object
            return carDataTmp;

        } catch (DataAccessException e) {
            //logger.error("Database error while saving car data: {}", e.getMessage(), e);
            throw new RuntimeException("Database error occurred", e);
        } catch (NullPointerException e) {
            //logger.error("Null pointer error while processing car data: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Received null data, please check the inputs.", e);
        } catch (Exception e) {
            //logger.error("Unexpected error while processing car data: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
