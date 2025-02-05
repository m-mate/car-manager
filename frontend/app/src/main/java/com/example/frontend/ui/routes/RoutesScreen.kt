package com.example.frontend.ui.routes



import android.content.Context
import android.util.Log
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
import com.example.frontend.RetrofitClient
import com.example.frontend.CarApiService
import com.example.frontend.model.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RoutesScreen(navController: NavController, carId: Int) {
    val context = LocalContext.current
    val routes = remember { mutableStateListOf<Route>() }

    // Fetch routes when the screen is launched
    LaunchedEffect(carId) {
        fetchRoutesForCar(context, carId, routes)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                Toast.makeText(context, "Live monitoring started!", Toast.LENGTH_SHORT).show()
                navController.navigate("dashboard")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Start Live Monitoring")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display routes in a LazyColumn
        if (routes.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(routes) { route ->
                    RouteItem(route = route, navController)
                }
            }
        } else {
            Text("No routes available", fontSize = 18.sp)
        }
    }
}

@Composable
fun RouteItem(route: Route, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("routeDetails/${route.id}") }
    ) {
        Text(text = "Route ID: ${route.id}", fontSize = 18.sp)
        Text(text = "Start: ${route.startTime}", fontSize = 14.sp)
        Text(text = "End: ${route.finishTime}", fontSize = 14.sp)
    }
}

// Function to fetch routes from API
private fun fetchRoutesForCar(context: Context, carId: Int, routeList: MutableList<Route>) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context,token).create(CarApiService::class.java)
    apiService.getAllRoutes(username, carId).enqueue(object : Callback<List<Route>> {
        override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    routeList.clear()
                    routeList.addAll(it)
                }
            } else {
                Toast.makeText(context, "No routes found or server error.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<Route>>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            Log.e("Error", "Error occurred: ${t.message}", t)

        }
    })
}
