package com.example.frontend.ui.cars

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.model.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarListViewModel @Inject constructor(
    application: Application, // If needed, use AndroidViewModel
    private val apiService: CarApiService
) : AndroidViewModel(application) {

    val _carList = MutableStateFlow<List<Car>>(emptyList()) // ✅ StateFlow for UI state
    val carList: StateFlow<List<Car>> = _carList

    private val _errorMessage = MutableStateFlow<String?>(null) // Error handling
    val errorMessage: StateFlow<String?> = _errorMessage

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun fetchCarsForUser() {
        val username = sharedPreferences.getString("username", null)
        val token = sharedPreferences.getString("jwt_token", null)

        if (username.isNullOrEmpty() || token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }
        val authToken = "Bearer $token"
        viewModelScope.launch {
            try {
                val response = apiService.getCarsByUser(username, authToken)

                when {
                    response.isSuccessful -> {
                        _carList.value = response.body() ?: emptyList() // ✅ Ensure empty list if null
                    }
                    response.code() == 401 -> {
                        _errorMessage.value = "Unauthorized access. Please log in again."
                        _carList.value = emptyList()
                    }
                    else -> {
                        _errorMessage.value = "Failed to fetch cars: ${response.message()}"
                        _carList.value = emptyList()
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Server error. Please try again later."
                _carList.value = emptyList()
            }
        }
    }


    fun deleteCar(carId: Int) {
        val token = sharedPreferences.getString("jwt_token", null)
        if ( token.isNullOrEmpty()) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }
        val authToken = "Bearer $token"
        viewModelScope.launch {
            try {
                apiService.deleteCar(carId, authToken)
                _carList.update { currentList ->
                    currentList.filterNot { it.id == carId }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error deleting car: ${e.message}"
            }
        }
    }
}