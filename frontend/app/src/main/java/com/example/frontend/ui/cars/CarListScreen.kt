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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.model.Car
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import com.example.frontend.ui.cars.CarListViewModel

@Composable
fun CarListScreen(navController: NavController, viewModel: CarListViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val carList by viewModel.carList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    LaunchedEffect(Unit) {
        if (!token.isNullOrEmpty() && !username.isNullOrEmpty()) {
            viewModel.fetchCarsForUser()
        } else {
            Toast.makeText(context, "Please log in again.", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage == "Unauthorized access. Please log in again.") {
            navController.navigate("login") {
                popUpTo("dashboard") { inclusive = true }
            }
        }else if (errorMessage == "Server error. Please try again later."){
            navController.navigate("server") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { navController.navigate("addCar") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Car")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (carList.isNotEmpty()) {
            LazyColumn {
                items(carList) { car ->
                    CarItem(
                        car = car,
                        onClick = {
                            sharedPreferences.edit().putInt("carId", car.id!!).apply()
                            sharedPreferences.edit().putString("vin", car.vin).apply()
                            navController.navigate("routes/${car.id}")
                        },
                        onDelete = { car.id?.let { viewModel.deleteCar(it) } }
                    )
                }
            }
        } else {
            Text("No cars available", fontSize = 18.sp)
        }

        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
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