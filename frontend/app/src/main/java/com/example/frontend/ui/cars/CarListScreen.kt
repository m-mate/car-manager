package com.example.frontend.ui.cars

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.Car


@Composable
fun CarListScreen(
    carList: List<Car>,
    onAddCarClicked: () -> Unit,
    onCarClicked: (Car) -> Unit
) {
    Log.d("CarListScreen", "carList size: ${carList.size}") // Log the size of the list

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add Car Button
        Button(
            onClick = onAddCarClicked,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Car")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Car List - LazyColumn to display the list of cars
        if (carList.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(carList) { car ->
                    CarItem(car = car, onClick = { onCarClicked(car) })
                }
            }
        } else {
            Text("No cars available")
        }
    }
}

@Composable
fun CarItem(car: Car, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = car.type,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "VIN: ${car.vin}",
            fontSize = 14.sp
        )
    }
}