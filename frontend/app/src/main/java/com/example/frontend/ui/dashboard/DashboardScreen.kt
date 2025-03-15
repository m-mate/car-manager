package com.example.frontend.ui.dashboard

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import com.example.frontend.customColors
import com.example.frontend.model.CarDataLive
import com.github.yamin8000.gauge.main.Gauge
import com.github.yamin8000.gauge.main.GaugeNumerics
import com.github.yamin8000.gauge.ui.color.GaugeArcColors
import com.github.yamin8000.gauge.ui.color.GaugeNeedleColors
import com.github.yamin8000.gauge.ui.color.GaugeTicksColors
import com.github.yamin8000.gauge.ui.style.GaugeArcStyle
import com.github.yamin8000.gauge.ui.style.GaugeNeedleStyle
import com.github.yamin8000.gauge.ui.style.GaugeStyle
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


@Composable
fun DashboardScreen(navController: NavHostController, viewModel: DashboardViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val token = sharedPreferences.getString("jwt_token", null)
    val vin = sharedPreferences.getString("vin", null)

    val carData by viewModel.carData.collectAsState()

    val activity = context as? android.app.Activity

    DisposableEffect(Unit) {
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    if (token.isNullOrEmpty() || vin.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCarData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (isPortrait) {
            // Portrait layout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .background(
                        color = Color(0xff242424),
                        shape = RoundedCornerShape(150.dp)
                    )
                    .padding(16.dp)
            ) {
                val animatedSpeed by animateFloatAsState(
                    targetValue = carData?.speed?.toFloat() ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                SpeedometerView(speed = animatedSpeed)

                Text(
                    text = "Fuel Rate: ${carData?.fuelRate ?: 0}l/100km",
                    color = Color.White
                )
                Text(
                    text = "Coolant Temp: ${carData?.coolantTemp ?: 0}°C",
                    color = Color.White
                )

                val animatedRPM by animateFloatAsState(
                    targetValue = carData?.rpm?.div(1000f) ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                RPMView(rpm = animatedRPM)
            }
        } else {
            // Landscape layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xff242424), shape = RoundedCornerShape(150.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val animatedSpeed by animateFloatAsState(
                    targetValue = carData?.speed?.toFloat() ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                SpeedometerView(speed = animatedSpeed)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Fuel Rate: ${carData?.fuelRate ?: 0}l/100km",
                        color = Color.White
                    )
                    Text(
                        text = "Coolant Temp: ${carData?.coolantTemp ?: 0}°C",
                        color = Color.White
                    )
                }

                val animatedRPM by animateFloatAsState(
                    targetValue = carData?.rpm?.div(1000f) ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                RPMView(rpm = animatedRPM)
            }
        }
    }
}


@Composable
fun SpeedometerView(speed: Float) {
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
                smallTicksStep = 5,
                bigTicksStep = 20,
            ),
            style = defaultGaugeStyle(),
            borderColor = customColors.onPrimary,
            centerCircleColor = Color(0xFF000000),
            valueColor = customColors.onPrimary,
            needleColors = GaugeNeedleColors(
                needle = Color(0xffff1100),
                ring = customColors.primary
            ),
            arcColors = GaugeArcColors(
                off = customColors.onPrimary,
                on = customColors.primary
            ),
            ticksColors = GaugeTicksColors(
                smallTicks = customColors.onPrimary,
                bigTicks = Color(0xffff1100),
                bigTicksLabels = customColors.onPrimary
            )

        )

}



@Composable
fun RPMView(rpm: Float) {
    Gauge(
        modifier = Modifier.size(250.dp),
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
        borderColor = customColors.onPrimary,
        centerCircleColor = Color(0xFF000000),
        valueColor = customColors.onPrimary,
        needleColors = GaugeNeedleColors(
            needle = Color(0xffff1100),
            ring = customColors.primary
        ),
        arcColors = GaugeArcColors(
            off = customColors.onPrimary,
            on = customColors.primary
        ),
        ticksColors = GaugeTicksColors(
            smallTicks = customColors.onPrimary,
            bigTicks = Color(0xffff1100),
            bigTicksLabels = customColors.onPrimary
        )
    )
}

@Composable
fun defaultGaugeStyle(): GaugeStyle {
    return GaugeStyle(
        hasBorder = true,
        hasValueText = false,
        borderWidth = 10f,
        arcStyle = GaugeArcStyle(
            hasArcs = true,
            hasProgressiveAlpha = false,
            bigTicksHasLabels = true,
            cap = StrokeCap.Round,
        ),
        needleStyle = GaugeNeedleStyle(
            hasNeedle = true,
            tipHasCircle = false,
            hasRing = true,
            ringWidth = 10f
        )
    )
}


