package com.example.car_manager.controller;


import com.example.car_manager.dto.RouteDetailsDTO;
import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarData;
import com.example.car_manager.model.Route;
import com.example.car_manager.model.User;
import com.example.car_manager.service.CarService;
import com.example.car_manager.service.RouteService;
import com.example.car_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;
    private final UserService userService;
    private final CarService carService;

    @Autowired
    public RouteController(RouteService routeService, UserService userService, CarService carService) {
        this.routeService = routeService;
        this.userService = userService;
        this.carService = carService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Route>> getAllRoutes(@RequestParam String username, @RequestParam Integer carId) {
        User user = userService.findByUsername(username);


        Car car = carService.findById(carId); 


        List<Route> routes = routeService.getAllRoutes(user, car);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{routeId}/details")
    public ResponseEntity<RouteDetailsDTO> getRouteDetails(@PathVariable Long routeId) {
        RouteDetailsDTO routeDetails = routeService.getRouteDetails(routeId);
        return ResponseEntity.ok(routeDetails);
    }


}

