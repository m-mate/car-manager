package com.example.car_manager.service;

import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarData;
import com.example.car_manager.model.Route;
import com.example.car_manager.model.User;
import com.example.car_manager.repo.CarDataRepository;
import com.example.car_manager.repo.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteService {
    RouteRepository routeRepository;
    CarDataRepository carDataRepository;

    @Autowired
    public void setCarDataRepository(CarDataRepository carDataRepository) {
        this.carDataRepository = carDataRepository;
    }

    @Autowired
    public void setRouteRepository(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> getAllRoutes(User user, Car car) {
        checkForNewRoute(user, car);
        return routeRepository.findByUserAndCar(user, car);
    }
    /*
    public void checkForNewRoute(User user, Car car) {
        List<CarData> carDataList = carDataRepository.findByCarAndInRouteFalseOrderByTimeStampAsc(car);
        List<Route> newRoutes = new ArrayList<>();
        int count = 0;
        int speed = 0;
        int fuelRate = 0;
        if (carDataList.isEmpty()) {
            return;
        }

        Route currentRoute = null;
        LocalDateTime previousTimestamp = null;

        for (CarData carData : carDataList) {
            if (previousTimestamp == null ||
                    Duration.between(previousTimestamp, carData.getTimeStamp()).toMinutes() > 30) {
                if (currentRoute != null) {
                    currentRoute.setFinishTime(previousTimestamp.toLocalTime());
                    newRoutes.add(currentRoute);
                }

                currentRoute = new Route();
                currentRoute.setStartTime(carData.getTimeStamp().toLocalTime());
                currentRoute.setCar(car);
                currentRoute.setUser(user);
                count += 1;
                speed += carData.getSpeed();
                fuelRate += carData.getFuelRate();
            }

            carData.setInRoute(true);
            carDataRepository.save(carData);
            previousTimestamp = carData.getTimeStamp();
        }


        if (currentRoute != null) {
            currentRoute.setFinishTime(previousTimestamp.toLocalTime());
            long totalMinutes = Duration.between(currentRoute.getStartTime(), currentRoute.getFinishTime()).toMinutes();
            double hours = totalMinutes / 60.0;
            int avgSpeed =  Math.round((float) speed /count);
            currentRoute.setAvgSpeed(avgSpeed);
            currentRoute.setDistanceTraveled((int) (avgSpeed * hours));
            double avgFuelRate = (double) fuelRate / count;
            currentRoute.setAvgFuelConsumption(avgFuelRate);
            currentRoute.setFuelUsed(avgFuelRate * hours);
            newRoutes.add(currentRoute);
        }
         count = 0;
         speed = 0;
         fuelRate = 0;

        routeRepository.saveAll(newRoutes);
    }
    */

    public void checkForNewRoute(User user, Car car) {
        List<CarData> carDataList = carDataRepository.findByCarAndInRouteFalseOrderByTimeStampAsc(car);
        List<Route> newRoutes = new ArrayList<>();

        if (carDataList.isEmpty()) {
            return;
        }

        Route currentRoute = null;
        LocalDateTime previousTimestamp = null;

        int count = 0;
        int speedSum = 0;
        int fuelRateSum = 0;

        for (CarData carData : carDataList) {
            LocalDateTime currentTimestamp = carData.getTimeStamp();

            if (previousTimestamp == null || Duration.between(previousTimestamp, currentTimestamp).toMinutes() > 30) {
                // Save previous route if it exists
                if (currentRoute != null) {
                    currentRoute.setFinishTime(previousTimestamp);

                    long totalMinutes = Duration.between(currentRoute.getStartTime(), previousTimestamp).toMinutes();
                    double hours = totalMinutes / 60.0;

                    int avgSpeed = (count > 0) ? Math.round((float) speedSum / count) : 0;
                    double avgFuelRate = (count > 0) ? (double) fuelRateSum / count : 0;

                    currentRoute.setAvgSpeed(avgSpeed);
                    currentRoute.setDistanceTraveled((int) (avgSpeed * hours));
                    currentRoute.setAvgFuelConsumption(avgFuelRate);
                    currentRoute.setFuelUsed(avgFuelRate * hours);

                    newRoutes.add(currentRoute);
                }

                // Start a new route
                currentRoute = new Route();
                currentRoute.setStartTime(currentTimestamp);
                currentRoute.setCar(car);
                currentRoute.setUser(user);

                // Reset counters for new route
                count = 0;
                speedSum = 0;
                fuelRateSum = 0;
            }

            // Accumulate data for the current route
            count++;
            speedSum += carData.getSpeed();
            fuelRateSum += carData.getFuelRate();

            carData.setInRoute(true);
            previousTimestamp = currentTimestamp;
        }

        // Save the last route
        if (currentRoute != null) {
            currentRoute.setFinishTime(previousTimestamp);

            long totalMinutes = Duration.between(currentRoute.getStartTime(), previousTimestamp).toMinutes();
            double hours = totalMinutes / 60.0;

            int avgSpeed = (count > 0) ? Math.round((float) speedSum / count) : 0;
            double avgFuelRate = (count > 0) ? (double) fuelRateSum / count : 0;

            currentRoute.setAvgSpeed(avgSpeed);
            currentRoute.setDistanceTraveled((int) (avgSpeed * hours));
            currentRoute.setAvgFuelConsumption(avgFuelRate);
            currentRoute.setFuelUsed(avgFuelRate * hours);

            newRoutes.add(currentRoute);
        }

        // Persist updates
        carDataRepository.saveAll(carDataList);
        routeRepository.saveAll(newRoutes);
    }



    public List<CarData> getRouteDetails(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        return carDataRepository.findByCarAndTimeStampBetween(
                route.getCar(),
                route.getStartTime(),
                route.getFinishTime()
        );
    }

}
