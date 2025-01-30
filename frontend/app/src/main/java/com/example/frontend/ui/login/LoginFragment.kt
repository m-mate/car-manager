package com.example.frontend.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.frontend.CarApiService
import com.example.frontend.R
import com.example.frontend.RetrofitClient
import com.example.frontend.User
import com.example.frontend.ui.login.LoginScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {
    /*private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Find the BottomNavigationView in the activity's layout
        bottomNavigationView = requireActivity().findViewById(R.id.nav_view)


        // Hide the Bottom Navigation in LoginFragment
        bottomNavigationView.visibility = View.GONE

        // Set up ComposeView
        return ComposeView(requireContext()).apply {
            setContent {
                LoginScreen(
                    onLogin = { username, password ->
                        handleLogin(username, password)
                    },
                    onRegister = {
                        findNavController().navigate(R.id.action_login_to_register)
                    }
                )
            }
        }
    }

    private fun handleLogin(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Both fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(username, password)
        val apiService = RetrofitClient.create("").create(CarApiService::class.java)

        apiService.loginUser(user).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val token = response.body()
                    if (!token.isNullOrEmpty()) {
                        // Save token to SharedPreferences
                        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("jwt_token", token).apply()
                        sharedPreferences.edit().putString("username", username).apply()

                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                        Log.d("LoginFragment", "JWT Token: $token")

                        // Navigate to CarListFragment
                        findNavController().navigate(R.id.action_login_to_carList)
                    } else {
                        Toast.makeText(requireContext(), "Empty token received", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Invalid credentials or server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("LoginError", "Error: ${t.message}", t)
                Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
*/

}

