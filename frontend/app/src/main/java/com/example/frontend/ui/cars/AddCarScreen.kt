package com.example.frontend.ui.cars

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend.model.Car
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddCarScreen(navController: NavController) {
    var carModel by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val saveCar: (String) -> Unit = { model ->
        if (model.isEmpty()) {
            Toast.makeText(context, "Car model cannot be empty", Toast.LENGTH_SHORT).show()

        }
        val car = Car(type = model, vin = "")
        saveCar(car,context, navController);

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = carModel,
            onValueChange = { carModel = it },
            label = { Text("Car Model") },
            modifier = Modifier.fillMaxWidth(),
            isError = carModel.isEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = { saveCar(carModel) },
            modifier = Modifier.fillMaxWidth(),
            enabled = carModel.isNotEmpty() && !isLoading
        ) {
            Text(text = if (isLoading) "Saving..." else "Add Car")
        }
    }
}

fun saveCar(car: Car, context: Context, navController: NavController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context,token).create(CarApiService::class.java)
    apiService.saveCar(username,car).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            val responseBody = response.body()
            if (response.isSuccessful) {

                    navController.navigate("carList")

            }else if (response.code() == 401) {
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



