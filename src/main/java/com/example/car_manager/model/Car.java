package com.example.car_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @OneToMany(mappedBy = "car")
    private List<Route> routes;

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", vin='" + vin + '\'' +
                ", type='" + type + '\'' +
                ", carData=" + carData +
                ", carDataTmp=" + carDataTmp +
                ", users=" + users +
                ", routes=" + routes +
                '}';
    }
}
