package com.example.frontend.ui.carlist

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend.Car
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CarListScreen(navController: NavController) {
    val context = LocalContext.current
    val carList = remember { mutableStateListOf<Car>() }
    val coroutineScope = rememberCoroutineScope()

    // Fetch cars when the screen is launched
    LaunchedEffect(Unit) {
        fetchCarsForUser(context, carList)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                navController.navigate("addCar") // Navigate to Add Car Screen
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Car")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display car list using LazyColumn
        if (carList.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(carList) { car ->
                    CarItem(car = car, onClick = {
                        Toast.makeText(context, "Car clicked: ${car.type}", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        } else {
            Text("No cars available", fontSize = 18.sp)
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
        Text(text = car.type, fontSize = 18.sp)
        Text(text = "VIN: ${car.vin}", fontSize = 14.sp)
    }
}

// Function to fetch cars from API
private fun fetchCarsForUser(context: Context, carList: MutableList<Car>) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(token).create(CarApiService::class.java)
    apiService.getCarsByUser(username).enqueue(object : Callback<List<Car>> {
        override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    carList.clear()
                    carList.addAll(it)
                }
            } else {
                Toast.makeText(context, "No cars found or server error.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<Car>>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
}
