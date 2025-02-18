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
    private Double speed;

    @Column
    private Integer rpm;


    @Column(name = "fuel_rate")
    private Double fuelRate;

    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "in_route")
    private Boolean inRoute = false;

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

                ", fuelRate=" + fuelRate +
                ", timeStamp=" + timeStamp +
                ", car=" + car +
                '}';
    }
}


