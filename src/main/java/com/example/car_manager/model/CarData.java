package com.example.car_manager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_data")
public class CarData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double speed;

    private Integer rpm;

    @Column(name = "fuel_level")
    private Double fuelLevel;

    @Column(name = "fuel_rate")
    private Double fuelRate;

    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "vin", referencedColumnName = "vin")
    private Car car;

    // Getters and setters
}

