package com.example.frontend

data class CarData(
    val id: Int,
    val speed: Double,
    val rpm: Int,
    val fuelRate: Double,
    val timeStamp: String,
    val inRoute: Boolean
)
