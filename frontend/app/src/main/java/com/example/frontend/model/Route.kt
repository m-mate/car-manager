package com.example.frontend.model


data class Route(
    val id: Int,
    val avgSpeed: Int,
    val distanceTraveled: Int,
    val avgFuelConsumption: Double,
    val fuelUsed: Double,
    val startTime: String,
    val finishTime: String,
    val carId: Int,
    val userId: Int
)
