package com.example.car_manager.dto;

import com.example.car_manager.model.CarData;
import com.example.car_manager.model.Route;

import java.util.List;

public class RouteDetailsDTO {
    private Route route;
    private List<CarData> carData;

    public RouteDetailsDTO(Route route, List<CarData> carData) {
        this.route = route;
        this.carData = carData;
    }

    public Route getRoute() {
        return route;
    }

    public List<CarData> getCarData() {
        return carData;
    }
}
