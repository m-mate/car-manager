package com.example.car_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car_data_tmp")
public class CarDataTmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double speed;

    private Integer rpm;

    @Column(name = "fuel_level")
    private Double fuelLevel;

    @Column(name = "fuel_rate")
    private Double fuelRate;

    @Column(name = "time_stamp", nullable = false, updatable = false)
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "vin", referencedColumnName = "vin")
    private Car car;

    @PrePersist
    protected void onCreate() {
        this.timeStamp = LocalDateTime.now();
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public Double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Double getFuelRate() {
        return fuelRate;
    }

    public void setFuelRate(Double fuelRate) {
        this.fuelRate = fuelRate;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }
}

