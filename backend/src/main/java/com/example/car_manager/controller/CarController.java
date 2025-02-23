package com.example.car_manager.controller;

import com.example.car_manager.dto.CarDto;
import com.example.car_manager.model.Car;
import com.example.car_manager.model.CarDataTmp;
import com.example.car_manager.service.CarService;
import com.example.car_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarController {

    CarService carService;
    private UserService userService;

    @Autowired
    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/save/{username}")
    public ResponseEntity<String> add(@RequestBody Car car, @PathVariable String username) {
        try {
            car.setUser(userService.findByUsername(username));
            carService.save(car);
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Something went wrong");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Integer id, @RequestBody Car carDetails) {
        try {
            Car updatedCar = carService.updateCar(id, carDetails);
            return ResponseEntity.ok(updatedCar);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/vin/{vin}")
    public ResponseEntity<Car> findByVin(@PathVariable String vin) {
        Car car = carService.findByVin(vin);
        if (car != null) {
            return ResponseEntity.ok(car);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> findById(@PathVariable Integer id) {
        Optional<Car> carOptional = Optional.ofNullable(carService.findById(id));
        return carOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CarDto>> findById(@PathVariable String username) {
        List<Car> cars = carService.findByUserName(username);

        if (cars == null || cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Convert Car entities to CarDto
        List<CarDto> carDtos = cars.stream().map(car -> {
            CarDto dto = new CarDto();
            dto.setId(car.getId().longValue()); // Ensure correct type conversion
            dto.setVin(car.getVin());
            dto.setType(car.getType());
            return dto;
        }).toList();

        return ResponseEntity.ok(carDtos);
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}



