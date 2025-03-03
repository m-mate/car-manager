package com.example.frontend.ui.dashboard

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.model.CarDataLive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService
) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _carData = MutableStateFlow<CarDataLive?>(null)
    val carData: StateFlow<CarDataLive?> = _carData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchCarData() {
        val vin = sharedPreferences.getString("vin", null)
        val token = sharedPreferences.getString("jwt_token", null)

        if (vin.isNullOrEmpty() || token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }

        val authToken = "Bearer $token"

        viewModelScope.launch {
            try {
                while (true) { // Keep fetching data every second
                    val response = apiService.getCarStatus(vin, authToken)

                    if (response.isSuccessful) {
                        _carData.value = response.body()
                    } else {
                        _errorMessage.value = "Failed to fetch car data: ${response.message()}"
                    }

                    delay(1000) // Refresh every second
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch car data: ${e.message}"
            }
        }
    }
}
