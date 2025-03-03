package com.example.frontend.ui.routes

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.model.RouteDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService
) : AndroidViewModel(application) {

    private val _routeDetails = MutableStateFlow<RouteDetails?>(null)
    val routeDetails: StateFlow<RouteDetails?> = _routeDetails

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val token = sharedPreferences.getString("jwt_token", null)

    fun fetchRouteDetails(routeId: Int) {
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }
        val authToken = "Bearer $token"



        viewModelScope.launch {
            try {
                val details = apiService.getRouteDetails(routeId, authToken)
                _routeDetails.value = details
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching route details: ${e.message}"
            }
        }
    }
}
