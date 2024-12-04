package com.example.car_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "car_data")
public class CarData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    @Getter
    @Setter
    private Double speed;

    @Column
    private Integer rpm;

    @Column(name = "fuel_level")
    private Double fuelLevel;

    @Column(name = "fuel_rate")
    private Double fuelRate;

    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "vin", referencedColumnName = "vin")
    @JsonIgnore
    private Car car;

    @Override
    public String toString() {
        return "CarData{" +
                "id=" + id +
                ", speed=" + speed +
                ", rpm=" + rpm +
                ", fuelLevel=" + fuelLevel +
                ", fuelRate=" + fuelRate +
                ", timeStamp=" + timeStamp +
                ", car=" + car +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}

