package com.example.car_manager.service;

import com.example.car_manager.model.Car;
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
    CarRepository carRepository;

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
    public void checkForRemainingData(Car car){



            List<CarDataTmp> carDataTmpList = carDataTmpRepository.findByCar_Vin(car.getVin());

            double avgSpeed = carDataTmpList.stream().mapToDouble(CarDataTmp::getSpeed).average().orElse(0.0);

            int avgRpm = (int) carDataTmpList.stream().mapToInt(CarDataTmp::getRpm).average().orElse(0);

            double avgFuelRate = carDataTmpList.stream().mapToDouble(CarDataTmp::getFuelRate).average().orElse(0.0);

            CarData carData = new CarData();
            carData.setSpeed(avgSpeed);
            carData.setRpm(avgRpm);
            carData.setFuelRate(avgFuelRate);
            carData.setTimeStamp(carDataTmpList.getLast().getTimeStamp());
            carData.setCar(car);


            carDataTmpRepository.deleteAllByCar_Vin(car.getVin());

            carDataRepository.save(carData);


    }


    @Transactional
    public CarDataTmp saveCarDataTmp(CarDataTmp carDataTmp) {



        try {

            carDataTmpRepository.save(carDataTmp);
            List<CarDataTmp> carDataTmpList = carDataTmpRepository.findByCar_Vin(carDataTmp.getCar().getVin());

            if (carDataTmpList.size() >= 60) {
                double avgSpeed = carDataTmpList.stream().mapToDouble(CarDataTmp::getSpeed).average().orElse(0.0);
                int avgRpm = (int) carDataTmpList.stream().mapToInt(CarDataTmp::getRpm).average().orElse(0);

                double avgFuelRate = carDataTmpList.stream().mapToDouble(CarDataTmp::getFuelRate).average().orElse(0.0);

                CarData carData = new CarData();
                carData.setSpeed(avgSpeed);
                carData.setRpm(avgRpm);
                carData.setFuelRate(avgFuelRate);
                carData.setTimeStamp(carDataTmp.getTimeStamp()); 
                carData.setCar(carDataTmp.getCar());




                carDataTmpRepository.deleteAllByCar_Vin(carDataTmp.getCar().getVin());

                carDataRepository.save(carData);
            }

            return carDataTmp;

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred", e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Received null data, please check the inputs.", e);
        } catch (Exception e) {
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
