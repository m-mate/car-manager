package com.example.frontend.ui.user

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.CarApiService
import com.example.frontend.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val apiService: CarApiService
) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> get() = _allUsers

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val username = sharedPreferences.getString("username", null)
    private val token = sharedPreferences.getString("jwt_token", null)

    private fun getAuthToken(): String? = token?.let { "Bearer $it" }

    fun fetchUser() {
        val authToken = getAuthToken()
        if (username.isNullOrEmpty() || authToken == null) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.getUser(username, authToken)
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _errorMessage.value = "Failed to fetch user"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun fetchAllUsers() {
        val authToken = getAuthToken()
        if (authToken == null) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.getAllUsers(authToken)
                if (response.isSuccessful) {
                    _allUsers.value = response.body().orEmpty()
                } else {
                    _errorMessage.value = "Failed to fetch users"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun updateUser(username: String, password: String, email: String) {
        val authToken = getAuthToken()
        if (authToken == null) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }

        viewModelScope.launch {
            try {
                val user = User(username, password, email)
                val response = apiService.updateUser(username, user, authToken)
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _errorMessage.value = "Update failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun deleteUser(userId: Int) {
        val authToken = getAuthToken()
        if (authToken == null) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.deleteUser(userId, authToken)
                if (response.isSuccessful) {
                    _allUsers.value = _allUsers.value.filterNot { it.id == userId }
                } else {
                    _errorMessage.value = "Delete failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun changeRole(userId: Int) {
        val authToken = getAuthToken()
        if (authToken == null) {
            _errorMessage.value = "User not logged in. Please log in again."
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.changeUserRole(userId, authToken)
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    if (updatedUser != null) {
                        _allUsers.value = _allUsers.value.map {
                            if (it.id == userId) updatedUser else it
                        }
                    }
                } else {
                    _errorMessage.value = "Role change failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
