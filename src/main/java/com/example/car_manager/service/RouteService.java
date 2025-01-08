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

    public void checkForNewRoute(User user, Car car) {
        List<CarData> carDataList = carDataRepository.findByCarAndInRouteFalseOrderByTimeStampAsc(car);
        List<Route> newRoutes = new ArrayList<>();

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
            }

            carData.setInRoute(true);
            carDataRepository.save(carData);
            previousTimestamp = carData.getTimeStamp();
        }


        if (currentRoute != null) {
            currentRoute.setFinishTime(previousTimestamp.toLocalTime());
            newRoutes.add(currentRoute);
        }


        routeRepository.saveAll(newRoutes);
    }

    public List<CarData> getRouteDetails(Long routeId) {
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        return carDataRepository.findByCarAndTimeStampBetween(
                route.getCar(),
                route.getStartTime().atDate(LocalDateTime.now().toLocalDate()),
                route.getFinishTime().atDate(LocalDateTime.now().toLocalDate())
        );
    }
}
