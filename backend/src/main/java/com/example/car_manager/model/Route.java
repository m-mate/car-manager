package com.example.car_manager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "avg_speed", nullable = false)
    private Integer avgSpeed;

    @Column(name = "distance_traveled", nullable = false)
    private Integer distanceTraveled;

    @Column(name = "avg_fuel_cons", nullable = false)
    private Double avgFuelConsumption;

    @Column(name = "fuel_used", nullable = false)
    private Double fuelUsed;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "finish_time", nullable = false)
    private LocalDateTime finishTime;

    @ManyToOne
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    @JsonBackReference
    private Car car;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;



}

