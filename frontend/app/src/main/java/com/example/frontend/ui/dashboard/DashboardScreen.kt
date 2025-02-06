package com.example.frontend.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
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
    var carData by remember { mutableStateOf<CarData?>(null) }
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    LaunchedEffect(Unit) {
        while (true) {
            fetchCarData { fetchedData ->
                carData = fetchedData
            }
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (isPortrait) {
            // Portrait layout (vertical)
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(topStart = 150.dp, topEnd = 150.dp, bottomStart = 150.dp, bottomEnd = 150.dp) // Fully rounded top
                    )
                    .padding(16.dp)
            ) {
                SpeedometerView(speed = 100.2365F)

                Text("Fuel Level: ${carData?.fuelRate ?: 0}%")
                Text("Coolant Temp: ${carData?.fuelRate ?: 0}°C")
                RPMView(rpm = 2.5F)

            }
        } else {
            // Landscape layout (horizontal)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(topStart = 150.dp, topEnd = 150.dp, bottomStart = 150.dp, bottomEnd = 150.dp)
                    ),

                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpeedometerView(speed = 100.2365F)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Fuel Level: ${carData?.fuelRate ?: 0}%")
                    Text("Coolant Temp: ${carData?.fuelRate ?: 0}°C")
                }
                RPMView(rpm = 2.5F)
            }
        }
    }
}

@Composable
fun SpeedometerView(speed: Float) {
     // Increased size
        Gauge(
            modifier = Modifier.size(250.dp),
            value = speed,
            valueUnit = "km/h",
            decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 },
            totalSize = 250.dp,  // Increased from 200dp
            numerics = GaugeNumerics(
                startAngle = 90,
                sweepAngle = 270,
                valueRange = 0f..220f,
                smallTicksStep = 1,
                bigTicksStep = 20
            ),
            style = defaultGaugeStyle(),
            borderColor = Color(0xFFFFAB00),
            centerCircleColor = Color(0xFFFF6D00),
            valueColor = Color(0xFFFFD600)
        )

}



@Composable
fun RPMView(rpm: Float) {
    Gauge(
        modifier = Modifier.size(250.dp) ,
        value = rpm,
        valueUnit = "RPM",
        decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 },
        totalSize = 250.dp,
        numerics = GaugeNumerics(
            startAngle = 90,
            sweepAngle = 270,
            valueRange = 0f..8f,
            smallTicksStep = 1,
            bigTicksStep = 1
        ),
        style = defaultGaugeStyle(),
        borderColor = Color(0xFFFFAB00),
        centerCircleColor = Color(0xFFFF6D00),
        valueColor = Color(0xFFFFD600)
    )
}

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

fun fetchCarData(callback: (CarData) -> Unit) {
    // Simulated data fetching function
}
