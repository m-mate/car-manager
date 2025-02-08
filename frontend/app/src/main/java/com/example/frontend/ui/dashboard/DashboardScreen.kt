package com.example.frontend.ui.dashboard

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.navigation.NavHostController
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import com.example.frontend.model.CarDataLive
import com.github.yamin8000.gauge.main.Gauge
import com.github.yamin8000.gauge.main.GaugeNumerics
import com.github.yamin8000.gauge.ui.style.GaugeArcStyle
import com.github.yamin8000.gauge.ui.style.GaugeNeedleStyle
import com.github.yamin8000.gauge.ui.style.GaugeStyle
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDateTime


@Composable
fun DashboardScreen(navController: NavHostController) {
    var carData by remember { mutableStateOf<CarDataLive?>(null) }
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val token = remember { sharedPreferences.getString("jwt_token", null) }
    val vin = remember { sharedPreferences.getString("vin", null) }
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    if (token.isNullOrEmpty() || vin.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    LaunchedEffect(Unit) {
        while (true) {
            fetchCarData(context, token, vin ) { fetchedData ->
                carData = fetchedData
            }
            delay(1000)
        }
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
                val animatedSpeed by animateFloatAsState(
                    targetValue = carData?.speed?.toFloat() ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                SpeedometerView(speed = animatedSpeed)

                Text("Fuel Level: ${carData?.fuelRate ?: 0}%")
                Text("Coolant Temp: ${carData?.coolantTemp ?: 0}°C")
                val animatedRPM by animateFloatAsState(
                    targetValue = carData?.rpm?.div(1000f) ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                RPMView(rpm = animatedRPM)


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
                val animatedSpeed by animateFloatAsState(
                    targetValue = carData?.speed?.toFloat() ?: 0f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
                SpeedometerView(speed = animatedSpeed)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Fuel Level: ${carData?.fuelRate ?: 0}%")
                    Text("Coolant Temp: ${carData?.coolantTemp ?: 0}°C")
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

fun fetchCarData(context: Context, token: String, vin: String, callback: (CarDataLive) -> Unit) {


    if (token.isEmpty() || vin.isEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)

    apiService.getCarStatus(vin).enqueue(object : Callback<CarDataLive> {
        override fun onResponse(call: Call<CarDataLive>, response: Response<CarDataLive>) {
            if (response.isSuccessful) {
                response.body()?.let { carData ->
                    callback(carData) // Pass data to UI
                } ?: Log.e("fetchCarData", "Response body is null")
            } else {
                Log.e("fetchCarData", "Error: ${response.code()} ${response.message()}")
            }
        }

        override fun onFailure(call: Call<CarDataLive>, t: Throwable) {
            Log.e("fetchCarData", "Failed to fetch car data: ${t.message}", t)
        }
    })
}

