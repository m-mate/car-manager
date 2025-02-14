package com.example.frontend.ui.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.compose.rememberNavController
import com.example.frontend.CarApiService
import com.example.frontend.R
import com.example.frontend.RetrofitClient
import com.example.frontend.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: androidx.navigation.NavHostController) {
    var serveraddress by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { handleLogin(username, password, context, navController) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("register") }) {
            Text("Don't have an account? Register")
        }
    }
}

fun handleLogin(username: String, password: String, context: Context, navController: androidx.navigation.NavHostController) {
    if (username.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Both fields are required", Toast.LENGTH_SHORT).show()
        return
    }


    val user = User(username, password)
    val apiService = RetrofitClient.create(context,"").create(CarApiService::class.java)

    apiService.loginUser(user).enqueue(object : Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val token = response.body()
                if (!token.isNullOrEmpty()) {
                    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("jwt_token", token).apply()
                    sharedPreferences.edit().putString("username", username).apply()

                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    Log.d("LoginScreen", "JWT Token: $token")

                    navController.navigate("carList"){popUpTo(0) { inclusive = true }}
                } else {
                    Toast.makeText(context, "Empty token received", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Invalid credentials or server error", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("LoginError", "Error: ${t.message}", t)
            Toast.makeText(context, "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
