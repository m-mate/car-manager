package com.example.frontend.ui.routes

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.model.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    application: Application, // If needed, use AndroidViewModel
    private val apiService: CarApiService
) : AndroidViewModel(application) {

    val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)





    fun fetchRoutes(carId: Int) {
        val username = sharedPreferences.getString("username", null)
        val token = sharedPreferences.getString("jwt_token", null)
        if (username.isNullOrEmpty() || token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }
        val authToken = "Bearer $token"
        viewModelScope.launch {
            try {
                val routeList = apiService.getAllRoutes(username, carId, authToken)
                _routes.value = routeList
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching routes: ${e.message}"
            }
        }
    }

    fun deleteRoute(routeId: Int) {
        val token = sharedPreferences.getString("jwt_token", null)
        if ( token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }
        val authToken = "Bearer $token"
        viewModelScope.launch {
            try {
                apiService.deleteRoute(routeId, authToken)
                _routes.value = _routes.value.filterNot { it.id == routeId } // Update state properly
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting route: ${e.message}"
            }
        }
    }

    fun refreshRoutes(username: String, carId: Int) {
        val token = sharedPreferences.getString("jwt_token", null)
        if ( token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }
        val authToken = "Bearer $token"
        viewModelScope.launch {
            try {
                apiService.refreshRoutes(username, carId, authToken)
                fetchRoutes(carId) // Fetch again after refreshing
            } catch (e: Exception) {
                _errorMessage.value = "Error refreshing routes: ${e.message}"
            }
        }
    }
}
