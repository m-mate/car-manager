package com.example.frontend.ui.cars

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.model.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCarViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService,
) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var uiState = mutableStateOf<CarUiState>(CarUiState.Idle)

    fun saveCar(carModel: String) {
        if (carModel.isEmpty()) {
            uiState.value = CarUiState.Error("Car model cannot be empty")
            return
        }

        val username = sharedPreferences.getString("username", null)
        val token = sharedPreferences.getString("jwt_token", null)

        if (username.isNullOrEmpty() || token.isNullOrEmpty()) {
            uiState.value = CarUiState.Error("User not logged in. Please log in again.")
            return
        }
        val authToken = "Bearer $token"

        val car = Car(type = carModel, vin = "")
        viewModelScope.launch {
            uiState.value = CarUiState.Loading

            try {
                val response = apiService.saveCar(username, car, authToken)

                if (response.isSuccessful) {
                    uiState.value = CarUiState.Success
                } else {
                    uiState.value =
                        CarUiState.Error("Failed to fetch cars: ${response.message()}")

                }

            } catch (e: Exception) {
                uiState.value = CarUiState.Error("Failed to fetch cars: ${e.message}")

            }

        }
    }


    sealed class CarUiState {
        object Idle : CarUiState()
        object Loading : CarUiState()
        object Success : CarUiState()
        data class Error(val message: String) : CarUiState()
    }
}