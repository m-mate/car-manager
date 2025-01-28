package com.example.frontend

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate executed")
        clearToken()
        setContentView(R.layout.activity_main)

        // Find NavHostFragment safely
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as? NavHostFragment ?: throw IllegalStateException("NavHostFragment not found")

        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)

        // Check if the user is logged in
        val isLoggedIn = isUserLoggedIn()
        Log.d("MainActivity", "User logged in: $isLoggedIn")
        navGraph.setStartDestination(
            if (isLoggedIn) R.id.carListFragment else R.id.loginFragment
        )

        navController.graph = navGraph
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.contains("jwt_token")
        Log.d("MainActivity", "isUserLoggedIn: $isLoggedIn")
        return isLoggedIn
    }

    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("jwt_token").apply()
    }

}
