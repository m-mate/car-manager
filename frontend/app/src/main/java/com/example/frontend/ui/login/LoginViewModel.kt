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
    application: Application
) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String, context: Context) {
        if (username.isEmpty() || password.isEmpty()) return

        val user = User(username, password)
        val apiService = RetrofitClient.create(getApplication<Application>().applicationContext, "").create(CarApiService::class.java)

        apiService.loginUser(user).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val token = response.body()
                    if (!token.isNullOrEmpty()) {
                        saveUserData(context, username, token)
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error("Empty token received")
                    }
                } else {
                    _loginState.value = LoginState.Error("Invalid credentials or server error")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _loginState.value = LoginState.Error("An error occurred: ${t.message}")
            }
        })
    }

    private fun saveUserData(context: Context, username: String, token: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("jwt_token", token)
            putString("username", username)
            apply()
        }
    }
}


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
