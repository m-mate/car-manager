package com.example.frontend.model


import java.time.LocalTime

data class Route(
    val id: Int,
    val avgSpeed: Int,
    val distanceTraveled: Int,
    val avgFuelConsumption: Double,
    val fuelUsed: Double,
    val startTime: String,  // Using String to store time in "HH:mm:ss" format
    val finishTime: String,
    val carId: Int,
    val userId: Int
)
