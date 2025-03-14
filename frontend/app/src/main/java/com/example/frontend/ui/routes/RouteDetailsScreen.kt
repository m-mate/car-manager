package com.example.frontend.ui.routes

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.RetrofitClient
import com.example.frontend.CarApiService
import com.example.frontend.model.CarData
import com.example.frontend.model.RouteDetails
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun RouteDetailsScreen(navController: NavController, routeId: Int, viewModel: RouteDetailsViewModel = hiltViewModel()) {
    val routeDetails by viewModel.routeDetails.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(routeId) {
        viewModel.fetchRouteDetails(routeId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFFDEE4E7))
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (!errorMessage.isNullOrEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        routeDetails?.let { details ->
            RouteChart(carData = details.carData)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Route Summary", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RouteInfoRow("Avg Speed", "${details.route.avgSpeed} km/h", "Distance", "${details.route.distanceTraveled} km")
                        RouteInfoRow("Fuel Consumption", "${details.route.avgFuelConsumption} L/100km", "Fuel Used", "${"%.2f".format(details.route.fuelUsed)} L")
                        RouteInfoRow("Duration", calculateDuration(details.route.startTime, details.route.finishTime), "Data Count", details.carData.size.toString())
                    }
                }
            }
        } ?: run {
            Text("Loading route details...", fontSize = 18.sp)
        }
    }
}


@Composable
fun RouteInfoRow(label1: String, value1: String, label2: String, value2: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RouteInfoItem(label1, value1)
        RouteInfoItem(label2, value2)
    }
}

@Composable
fun RouteInfoItem(label: String, value: String) {
    Column {
        Text(label, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(value)
    }
}




@Composable
fun RouteChart(carData: List<CarData>) {
    var selectedDataType by remember { mutableStateOf("Speed") }
    val speedValues = carData.map { it.speed.toDouble() }
    val rpmValues = carData.map { it.rpm.toDouble() }
    val fuelRateValues = carData.map { it.fuelRate.toDouble() }

    val selectedValues = when (selectedDataType) {
        "Speed" -> speedValues
        "RPM" -> rpmValues
        "Fuel Rate" -> fuelRateValues
        else -> speedValues
    }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            elevation = 8.dp,
            backgroundColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(color = Color(0xFFffffff))

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DataToggleButton("Speed", selectedDataType) {
                        selectedDataType = it
                    }
                    DataToggleButton("RPM", selectedDataType) {
                        selectedDataType = it
                    }
                    DataToggleButton("Fuel Rate", selectedDataType) {
                        selectedDataType = it
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 22.dp),
                    data = remember(selectedValues) {
                        listOf(
                            Line(
                                label = selectedDataType,
                                values = selectedValues,
                                color = SolidColor(
                                    when (selectedDataType) {
                                        "Speed" -> Color(0xFF23af92)
                                        "RPM" -> Color(0xFFff8c00)
                                        "Fuel Rate" -> Color(0xFFf44336)
                                        else -> Color(0xFF23af92)
                                    }
                                ),
                                firstGradientFillColor = when (selectedDataType) {
                                    "Speed" -> Color(0xFF2BC0A1).copy(alpha = .5f)
                                    "RPM" -> Color(0xFFFF9E3A).copy(alpha = .5f)
                                    "Fuel Rate" -> Color(0xFFf44336).copy(alpha = .5f)
                                    else -> Color(0xFF2BC0A1).copy(alpha = .5f)
                                },
                                secondGradientFillColor = Color.Transparent,
                                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                gradientAnimationDelay = 1000,
                                drawStyle = DrawStyle.Stroke(width = 2.dp),
                            )
                        )
                    },
                    animationMode = AnimationMode.Together(delayBuilder = {
                        it * 500L
                    }),
                )
            }
        }
        }



@Composable
fun DataToggleButton(label: String, selectedDataType: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(label) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selectedDataType == label) Color.Gray else Color.LightGray
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = label)
    }
}


fun calculateDuration(startTime: String, finishTime: String): String {
    return try {
        val cleanedStartTime = startTime.substringBefore('.')
        val cleanedFinishTime = finishTime.substringBefore('.')
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        val start = format.parse(cleanedStartTime)!!
        val finish = format.parse(cleanedFinishTime)!!

        val durationMillis = finish.time - start.time
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60

        "$hours h $minutes min"
    } catch (e: Exception) {
        println(e.message)
        "Invalid time"
    }
}