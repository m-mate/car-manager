package com.example.car_manager.service;

import com.example.car_manager.dto.RouteDetailsDTO;
import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarData;
import com.example.car_manager.model.Route;
import com.example.car_manager.model.User;
import com.example.car_manager.repo.CarDataRepository;
import com.example.car_manager.repo.RouteRepository;
import jakarta.transaction.Transactional;
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
        //checkForNewRoute(user, car);
        return routeRepository.findByUserAndCar(user, car);
    }


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



    public RouteDetailsDTO getRouteDetails(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        System.out.println("Car VIN: " + route.getCar().getVin());
        System.out.println("Start Time: " + route.getStartTime());
        System.out.println("Finish Time: " + route.getFinishTime());



        List<CarData> carDataList = carDataRepository.findByCarAndTimeStampBetween(
                route.getCar(),
                route.getStartTime(),
                route.getFinishTime()
        );

        return new RouteDetailsDTO(route, carDataList);
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        Route route = routeRepository.findById(routeId).get();
        carDataRepository.deleteByCarAndTimeStampBetween(route.getCar(), route.getStartTime(), route.getFinishTime());
        routeRepository.deleteById(routeId);

    }

}
