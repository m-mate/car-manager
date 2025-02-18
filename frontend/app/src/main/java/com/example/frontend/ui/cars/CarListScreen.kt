package com.example.frontend.ui.carlist

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend.model.Car
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CarListScreen(navController: NavController) {
    val context = LocalContext.current
    val carList = remember { mutableStateListOf<Car>() }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        fetchCarsForUser(navController,context, carList)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                navController.navigate("addCar")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Car")
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (carList.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(carList) { car ->
                    CarItem(
                        car = car,
                        onClick = {
                            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            if (car.id != null){
                            sharedPreferences.edit().putInt("carId", car.id).apply()
                                sharedPreferences.edit().putString("vin", car.vin).apply()
                            navController.navigate("routes/${car.id}")
                            }
                                  },
                        onDelete = { car.id?.let { deleteCar(navController, context, it, carList) } } // Pass delete function
                    )
                }
            }
        } else {
            Text("No cars available", fontSize = 18.sp)
        }
    }
}

@Composable
fun CarItem(car: Car, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(text = car.type, fontSize = 18.sp)
                Text(text = "VIN: ${car.vin}", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Car",
                    tint = MaterialTheme.colors.error
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Car?") },
            text = { Text("Are you sure you want to delete this car? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


private fun fetchCarsForUser(navController: NavController, context: Context, carList: MutableList<Car>) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
    apiService.getCarsByUser(username).enqueue(object : Callback<List<Car>> {
        override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    carList.clear()
                    carList.addAll(it)
                }
            } else if (response.code() == 401) {
                sharedPreferences.edit().remove("jwt_token").apply()
                sharedPreferences.edit().clear().apply()
                Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("dashboard") { inclusive = true } // Clear backstack
                }
            } else {
                Toast.makeText(context, "No routes found or server error.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<Car>>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
}

private fun deleteCar(navController: NavController, context: Context, carId: Int, carList: MutableList<Car>) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)

    if (token.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
    apiService.deleteCar(carId).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                carList.removeAll { it.id == carId } // Remove deleted car from list
                Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show()
            } else if (response.code() == 401) {
                sharedPreferences.edit().remove("jwt_token").apply()
                sharedPreferences.edit().clear().apply()
                Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("dashboard") { inclusive = true } // Clear backstack
                }
            } else {
                Toast.makeText(context, "No routes found or server error.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
}
