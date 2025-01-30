package com.example.frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.frontend.ui.login.LoginScreen
import com.example.frontend.ui.register.RegisterScreen

import com.example.frontend.ui.carlist.CarListScreen
import com.example.frontend.ui.cars.AddCarScreen

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

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("carList") { CarListScreen(navController) }
        composable("addCar") { AddCarScreen(navController) }
    }
}

@Composable
fun isUserLoggedIn(): Boolean {
    val sharedPreferences =
        androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.contains("jwt_token")
}






