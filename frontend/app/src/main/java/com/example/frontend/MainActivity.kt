package com.example.frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.ui.BottomNavigationBar

import com.example.frontend.ui.login.LoginScreen
import com.example.frontend.ui.register.RegisterScreen

import com.example.frontend.ui.carlist.CarListScreen
import com.example.frontend.ui.cars.AddCarScreen
import com.example.frontend.ui.dashboard.DashboardScreen
import com.example.frontend.ui.login.ServerScreen
import com.example.frontend.ui.routes.RouteDetailsScreen
import com.example.frontend.ui.routes.RoutesScreen
import com.example.frontend.ui.user.UserScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate executed")
        //clearToken()

        setContent {

                MainNavigation()

        }
    }

    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("jwt_token").apply()
        sharedPreferences.edit().remove("server_address").apply()

    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val startDestination = if (!isServerSet()) "server" else if (isUserLoggedIn()) "carList" else "login"
    var showBottomNav by remember { mutableStateOf(false) }
    var showBackButton by remember { mutableStateOf(false) }
    var showMenuButton by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    val CustomPrimaryColor = Color(0xff949494)
    val CustomSecondaryColor = Color(0xFF03DAC6)
    val CustomBackgroundColor = Color(0xFFF5F5F5)
    val CustomOnPrimaryColor = Color(0xFFFFFFFF)
    val CustomOnBackgroundColor = Color(0xFF000000)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Manager") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                } else null,
                actions = {
                    if (showMenuButton) {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(onClick = {menuExpanded = false; navController.navigate("user")  }) {
                                Text("User Settings")
                            }
                            DropdownMenuItem(onClick = {menuExpanded = false; navController.navigate("carList") { popUpTo("carList") { inclusive = true } } }) {
                                Text("Change Car")
                            }
                            DropdownMenuItem(onClick = {
                                menuExpanded = false
                                navController.navigate("login") { popUpTo("login") { inclusive = true } }
                            }) {
                                Text("Log Out")
                            }
                            DropdownMenuItem(onClick = {
                                menuExpanded = false
                                navController.navigate("server") { popUpTo("server") { inclusive = true } }
                            }) {
                                Text("Change Server")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(navController, LocalContext.current)
            }
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding) // Ensure content is not hidden
        ) {
            composable("server"){showBackButton = false; showBottomNav = false; ServerScreen(navController)}
            composable("home") { CarListScreen(navController) }
            composable("dashboard") {showBackButton = true; showBottomNav = false; DashboardScreen(navController) }
            //composable("notifications") { NotificationsScreen(navController) }
            composable("login") {showBottomNav = false; showMenuButton = false; LoginScreen(navController)}
            composable("register") { RegisterScreen(navController) }
            composable("carList") {showBottomNav = false; showMenuButton = false; CarListScreen(navController) }
            composable("addCar") { showBackButton = true;  AddCarScreen(navController) }
            composable("user") { showBackButton = true;  UserScreen(navController) }
            composable("routeDetails/{routeId}"){backStackEntry ->
                val routeId = backStackEntry.arguments?.getString("routeId")?.toIntOrNull() ?: 0
                showBackButton = true; showMenuButton = true; showBottomNav = true; RouteDetailsScreen(navController, routeId)
            }
            composable("routes/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId")?.toIntOrNull() ?: 0
                showBackButton = false; showMenuButton = true; showBottomNav = true; RoutesScreen(navController, carId)
            }
        }
    }
}




@Composable
fun isUserLoggedIn(): Boolean {
    val sharedPreferences =
        LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.contains("jwt_token")
}

@Composable
fun isServerSet(): Boolean {
    val sharedPreferences =
        LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    println(sharedPreferences.getString("server_address",""))
    return sharedPreferences.contains("server_address")
}








