package com.example.frontend.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.frontend.CarData
import com.github.yamin8000.gauge.main.Gauge
import com.github.yamin8000.gauge.main.GaugeNumerics
import com.github.yamin8000.gauge.ui.color.GaugeArcColors
import com.github.yamin8000.gauge.ui.color.GaugeNeedleColors
import com.github.yamin8000.gauge.ui.color.GaugeTicksColors
import com.github.yamin8000.gauge.ui.style.GaugeArcStyle
import com.github.yamin8000.gauge.ui.style.GaugeNeedleStyle
import com.github.yamin8000.gauge.ui.style.GaugeStyle
import kotlinx.coroutines.delay
import java.text.DecimalFormat

@Composable
fun DashboardScreen(navController: NavHostController) {
    // Mutable state for holding car data
    var carData by remember { mutableStateOf<CarData?>(null) }

    // Simulate fetching car data every second
    LaunchedEffect(Unit) {
        while (true) {
            fetchCarData { fetchedData ->
                carData = fetchedData
            }
            delay(1000) // Fetch every 1 second
        }
    }

    // Dashboard layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Row for Speed and RPM Gauges side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeedometerView(speed = 200.2365F)
            RPMView(rpm = 2500F)
        }

        // Fuel Level and Coolant Temperature
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Fuel Level: ${carData?.fuelRate ?: 0}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Coolant Temp: ${carData?.fuelRate ?: 0}°C",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Speedometer Gauge
@Composable
fun SpeedometerView(speed: Float) {
    Gauge(
        modifier = Modifier.size(200.dp),
        value = speed,
        valueUnit = "km/h",
        decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 },
        totalSize = 250.dp,
        numerics = GaugeNumerics(
            startAngle = 150,
            sweepAngle = 240,
            valueRange = 0f..240f,
            smallTicksStep = 10,
            bigTicksStep = 20
        ),
        style = defaultGaugeStyle(),
        borderColor = Color(0xFFFFAB00),
        centerCircleColor = Color(0xFFFF6D00),
        valueColor = Color(0xFFFFD600)
    )
}

// RPM Gauge
@Composable
fun RPMView(rpm: Float) {
    Gauge(
        modifier = Modifier.size(200.dp),
        value = rpm,
        valueUnit = "RPM",
        decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 },
        totalSize = 250.dp,
        numerics = GaugeNumerics(
            startAngle = 150,
            sweepAngle = 240,
            valueRange = 0f..8000f,
            smallTicksStep = 500,
            bigTicksStep = 1000
        ),
        style = defaultGaugeStyle(),
        borderColor = Color(0xFFFFAB00),
        centerCircleColor = Color(0xFFFF6D00),
        valueColor = Color(0xFFFFD600)
    )
}

// Common Gauge Style
@Composable
fun defaultGaugeStyle(): GaugeStyle {
    return GaugeStyle(
        hasBorder = true,
        hasValueText = true,
        borderWidth = 10f,
        arcStyle = GaugeArcStyle(
            hasArcs = true,
            hasProgressiveAlpha = false,
            bigTicksHasLabels = true,
            cap = StrokeCap.Round
        ),
        needleStyle = GaugeNeedleStyle(
            hasNeedle = true,
            tipHasCircle = true,
            hasRing = true,
            ringWidth = 10f
        )
    )
}

// Simulated Function to Fetch Data (Replace with API call)
fun fetchCarData(callback: (CarData) -> Unit) {

}
