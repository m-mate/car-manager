package com.example.frontend.ui.login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    private val _serverIp = MutableStateFlow(sharedPreferences.getString("server_address", "") ?: "")
    val serverIp: StateFlow<String> = _serverIp

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting

    fun saveServerIp(serverAddress: String, onSuccess: () -> Unit) {
        if (serverAddress.isBlank()) {
            _errorMessage.value = "Please enter a valid server address."
            return
        }

        sharedPreferences.edit().putString("server_address", serverAddress).apply()
        _serverIp.value = serverAddress

        // Force Retrofit to recreate
        RetrofitClient.create(getApplication(),"")

        _isConnecting.value = true

        viewModelScope.launch {
            try {
                val response = apiService.pingServer()
                if (response.isSuccessful) {
                    _errorMessage.value = null
                    onSuccess()
                } else {
                    _errorMessage.value = "Invalid server address."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to connect: ${e.message}"
            } finally {
                _isConnecting.value = false
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}


