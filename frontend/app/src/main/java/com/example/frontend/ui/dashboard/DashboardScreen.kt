package com.example.frontend.ui.dashboard

import android.os.Handler
import android.os.Looper
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.github.yamin8000.gauge.main.Gauge
import com.github.yamin8000.gauge.*
import com.example.frontend.CarData
import com.github.yamin8000.gauge.main.GaugeNumerics
import com.github.yamin8000.gauge.ui.color.GaugeArcColors
import com.github.yamin8000.gauge.ui.color.GaugeNeedleColors
import com.github.yamin8000.gauge.ui.color.GaugeTicksColors
import com.github.yamin8000.gauge.ui.style.GaugeArcStyle
import com.github.yamin8000.gauge.ui.style.GaugeNeedleStyle
import com.github.yamin8000.gauge.ui.style.GaugeStyle
import java.text.DecimalFormat

@Composable
fun DashboardScreen(navController: NavHostController) {
    // MutableState to hold the car data
    var carData by remember { mutableStateOf<CarData?>(null) }

    // Handler to fetch data every second
    val handler = remember { Handler(Looper.getMainLooper()) }
    /*val updateRunnable: Runnable = remember {
        object : Runnable {
            override fun run() {

                fetchCarData { fetchedData ->
                    carData = fetchedData
                }
                handler.postDelayed(this, 1000) // Repeat every 1000 ms (1 second)
            }
        }
    }
*/
    // Start fetching data when the composable is first composed
    LaunchedEffect(Unit) {
        //handler.post(updateRunnable)
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeedometerView(speed = carData?.speed ?: 0f, modifier = Modifier.weight(1f))
            RPMView(rpm = carData?.rpm ?: 0f, modifier = Modifier.weight(1f))
        }

        // Fuel Level and Coolant Temperature Text below the gauges
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Fuel Level: ${carData?.fuelLevel ?: 0}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Coolant Temp: ${carData?.coolantTemp ?: 0}°C",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun SpeedometerView(speed: Number, modifier: Modifier = Modifier) {
    Gauge(
        modifier = Modifier.padding(8.dp),
        value = 55.35f,
        valueUnit = "km/h",
        decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 },
        totalSize = 500.dp,
        borderInset = 16.dp,
        numerics = GaugeNumerics(
            startAngle = 150,
            sweepAngle = 240,
            valueRange = 0f..220f,
            smallTicksStep = 1,
            bigTicksStep = 20
        ),
        style = GaugeStyle(
            hasBorder = true,
            hasValueText = true,
            borderWidth = 10f,
            arcStyle = GaugeArcStyle(
                hasArcs = true,
                hasProgressiveAlpha = false,
                bigTicksHasLabels = true,
                cap = StrokeCap.Square
            ),
            needleStyle = GaugeNeedleStyle(
                hasNeedle = true,
                tipHasCircle = true,
                hasRing = true,
                ringWidth = 10f
            )
        ),
        borderColor = Color(0xFFFFAB00),
        centerCircleColor = Color(0xFFFF6D00),
        valueColor = Color(0xFFFFD600),
        needleColors = GaugeNeedleColors(
            needle = Color(0xFFFFD600),
            ring = Color(0xFFFF6D00)
        ),
        arcColors = GaugeArcColors(
            off = Color(0xFFFFD600),
            on = Color(0xFF00C853)
        ),
        ticksColors = GaugeTicksColors(
            smallTicks = Color(0xFFFF6D00),
            bigTicks = Color(0xFFDD2C00),
            bigTicksLabels = Color(0xFFFFAB00)
        ),
        arcColorsProvider = { colors, gaugeValue, range ->
            when (gaugeValue) {
                in range.start..range.endInclusive / 4 -> GaugeArcColors(
                    colors.off,
                    Color.Red
                )

                in range.endInclusive / 4..range.endInclusive / 2 -> GaugeArcColors(
                    colors.off,
                    Color.Yellow
                )

                in range.endInclusive / 2..range.endInclusive * 3 / 4 -> GaugeArcColors(
                    colors.off,
                    Color(0xFFFF8000)
                )

                else -> GaugeArcColors(colors.off, Color.Green)
            }
        },
        ticksColorProvider = {
            it.map { pair ->
                if (pair.first % 15 == 0)
                    pair.first to Color(0xFF2962FF)
                else pair
            }
        }
    )
}


@Composable
fun RPMView(rpm: Number, modifier: Modifier = Modifier) {
    Gauge(
        modifier = Modifier.padding(8.dp),
        value = 55.35f,
        valueUnit = "rpm",
        decimalFormat = DecimalFormat().apply { maximumFractionDigits = 1 },
        totalSize = 500.dp,
        borderInset = 16.dp,
        numerics = GaugeNumerics(
            startAngle = 150,
            sweepAngle = 240,
            valueRange = 0f..6000f,
            smallTicksStep = 1,
            bigTicksStep = 20
        ),
        style = GaugeStyle(
            hasBorder = true,
            hasValueText = true,
            borderWidth = 10f,
            arcStyle = GaugeArcStyle(
                hasArcs = true,
                hasProgressiveAlpha = false,
                bigTicksHasLabels = true,
                cap = StrokeCap.Square
            ),
            needleStyle = GaugeNeedleStyle(
                hasNeedle = true,
                tipHasCircle = true,
                hasRing = true,
                ringWidth = 10f
            )
        ),
        borderColor = Color(0xFFFFAB00),
        centerCircleColor = Color(0xFFFF6D00),
        valueColor = Color(0xFFFFD600),
        needleColors = GaugeNeedleColors(
            needle = Color(0xFFFFD600),
            ring = Color(0xFFFF6D00)
        ),
        arcColors = GaugeArcColors(
            off = Color(0xFFFFD600),
            on = Color(0xFF00C853)
        ),
        ticksColors = GaugeTicksColors(
            smallTicks = Color(0xFFFF6D00),
            bigTicks = Color(0xFFDD2C00),
            bigTicksLabels = Color(0xFFFFAB00)
        ),
        arcColorsProvider = { colors, gaugeValue, range ->
            when (gaugeValue) {
                in range.start..range.endInclusive / 4 -> GaugeArcColors(
                    colors.off,
                    Color.Red
                )

                in range.endInclusive / 4..range.endInclusive / 2 -> GaugeArcColors(
                    colors.off,
                    Color.Yellow
                )

                in range.endInclusive / 2..range.endInclusive * 3 / 4 -> GaugeArcColors(
                    colors.off,
                    Color(0xFFFF8000)
                )

                else -> GaugeArcColors(colors.off, Color.Green)
            }
        },
        ticksColorProvider = {
            it.map { pair ->
                if (pair.first % 15 == 0)
                    pair.first to Color(0xFF2962FF)
                else pair
            }
        }
    )
}



