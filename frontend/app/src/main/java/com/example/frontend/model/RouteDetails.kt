package com.example.frontend.model

import com.example.frontend.CarData

data class RouteDetails(
    val route: Route,
    val carData: List<CarData>
)
