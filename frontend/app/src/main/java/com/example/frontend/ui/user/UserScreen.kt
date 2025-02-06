package com.example.frontend.ui.user

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.frontend.RetrofitClient
import com.example.frontend.User
import com.example.frontend.CarApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UserScreen(navController: NavHostController) {
    val context = LocalContext.current
    var user by remember { mutableStateOf<User?>(null) }

    // Initialize with empty strings to avoid null
    var usernameState by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") } // Error message to display in the UI

    // Fetch user when the screen loads
    LaunchedEffect(Unit) {
        user = fetchUser(context)
        user?.let {
            usernameState = it.username
            emailState = it.email
        } ?: run {
            errorMessage = "Failed to load user data."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Details", style = MaterialTheme.typography.h6)

        // Display error if any
        errorMessage.takeIf { it.isNotEmpty() }?.let {
            Text(text = it, color = MaterialTheme.colors.error)
        }

        // Username (Non-editable)
        OutlinedTextField(
            value = usernameState,
            onValueChange = { usernameState = it },
            label = { Text("Username") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Email Input
        OutlinedTextField(
            value = emailState,
            onValueChange = { emailState = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Save Button
        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    updateUser(context, password, emailState)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            } else {
                Text("Save Changes")
            }
        }
    }
}

suspend fun fetchUser(context: Context): User? {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    return try {
        val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
        val response: Response<User> = withContext(Dispatchers.IO) {
            apiService.getUser(username).execute()
        }

        if (response.isSuccessful) {
            response.body()
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "User not found or server error.", Toast.LENGTH_SHORT).show()
            }
            null
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null
    }
}

private fun updateUser(context: Context,  password: String, email: String) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)
    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val updatedUser = User(username, password, email)

    try {
        println("Start")
        val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
        println("Start2")
        apiService.updateUser(username, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Handle success: If the update is successful, show a success message
                    Toast.makeText(context, "User details updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failure: Show error message if the update fails
                    Toast.makeText(context, "Failed to update user details. ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Handle failure: Show error message if the request fails
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
