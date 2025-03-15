package com.example.frontend.ui.login

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.frontend.CarApiService
import com.example.frontend.MainApplication
import com.example.frontend.RetrofitClient
import com.example.frontend.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService
) : AndroidViewModel(application) {

    private val _registrationStatus = mutableStateOf<String?>(null)
    val registrationStatus: State<String?> = _registrationStatus
    fun registerUser(username: String, email: String, password: String, navController: NavHostController) {
        if (!validateInputs(username, email, password)) {
            _registrationStatus.value = "Invalid inputs!"
            return
        }

        val user = User(username, password, email)

        viewModelScope.launch {
            try {
                val response = apiService.registerUser(user)

                when {
                    response.isSuccessful -> {
                        _registrationStatus.value = "Success"
                    }
                    response.code() == 409 -> {
                        _registrationStatus.value = "Username already taken. Please choose another one."
                    }
                    else -> {
                        _registrationStatus.value = "Failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _registrationStatus.value = "Error: ${e.message}"
            }
        }
    }


    private fun validateInputs(username: String, email: String, password: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) return false
        if (!isValidEmail(email)) return false
        if (password.length < 6) return false
        return true
    }

    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}


