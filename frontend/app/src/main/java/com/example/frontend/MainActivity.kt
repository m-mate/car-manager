package com.example.frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.ui.BottomNavigationBar

import com.example.frontend.ui.login.LoginScreen
import com.example.frontend.ui.register.RegisterScreen

import com.example.frontend.ui.carlist.CarListScreen
import com.example.frontend.ui.cars.AddCarScreen
import com.example.frontend.ui.dashboard.DashboardScreen
import com.example.frontend.ui.routes.RoutesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate executed")
        clearToken()

        setContent {

                MainNavigation()

        }
    }

    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("jwt_token").apply()
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val startDestination = if (isUserLoggedIn()) "carList" else "login"
    var showBottomNav by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Manager") }, // Set title
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        },
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding) // Ensure content is not hidden
        ) {
            composable("home") { CarListScreen(navController) }
            composable("dashboard") { DashboardScreen(navController) }
            //composable("notifications") { NotificationsScreen(navController) }
            composable("login") {showBottomNav = false; LoginScreen(navController)}
            composable("register") { RegisterScreen(navController) }
            composable("carList") {showBottomNav = true; CarListScreen(navController) }
            composable("addCar") { AddCarScreen(navController) }
            composable("routes/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId")?.toIntOrNull() ?: 0
            RoutesScreen(navController, carId)
            }
        }
    }
}

@Composable
fun isUserLoggedIn(): Boolean {
    val sharedPreferences =
        androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.contains("jwt_token")
}






