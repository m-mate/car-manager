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
@Table(name = "car_data_tmp")
public class CarDataTmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double speed;

    private Integer rpm;

    private Integer coolantTemp;


    @Column(name = "fuel_rate")
    private Double fuelRate;

    @Column(name = "time_stamp", nullable = false, updatable = false)
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "vin", referencedColumnName = "vin")
    @JsonIgnore
    private Car car;

    @PrePersist
    protected void onCreate() {
        this.timeStamp = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "CarDataTmp{" +
                "id=" + id +
                ", speed=" + speed +
                ", rpm=" + rpm +

                ", fuelRate=" + fuelRate +
                ", timeStamp=" + timeStamp +
                ", car=" + car +
                '}';
    }
}

