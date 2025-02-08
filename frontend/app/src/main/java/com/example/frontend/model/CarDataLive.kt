package com.example.frontend.model

data class CarDataLive(
    val id: Int,
    val speed: Double,
    val rpm: Int,
    val fuelRate: Double,
    val coolantTemp: Int,
    val timeStamp: String,
    val inRoute: Boolean
)
