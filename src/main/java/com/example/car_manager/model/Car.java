package com.example.car_manager.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String vin;

    private String type;

    @OneToMany(mappedBy = "car")
    private List<CarData> carData;

    @OneToMany(mappedBy = "car")
    private List<CarDataTmp> carDataTmp;

    @OneToMany(mappedBy = "car")
    private List<User> users;

    @OneToMany(mappedBy = "car")
    private List<Route> routes;


}
