package com.example.frontend.ui.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import com.example.frontend.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService
) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    fun login(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Invalid inputs!")
            return
        }

        val user = User(username, password)

        viewModelScope.launch {
            try {
                val response = apiService.loginUser(user) // Assuming a suspend function
                if (response.isSuccessful) {
                    val token = response.body()
                    if (!token.isNullOrEmpty()) {
                        saveUserData(username, token)
                        _loginState.value = LoginState.Success

                    } else {
                        _loginState.value = LoginState.Error("Empty token received")
                    }
                } else {
                    _loginState.value = LoginState.Error("Invalid credentials or server error")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }

    private fun saveUserData(username: String, token: String) {
        val sharedPreferences = getApplication<Application>()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("jwt_token", token)
            putString("username", username)
            apply()
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

}
