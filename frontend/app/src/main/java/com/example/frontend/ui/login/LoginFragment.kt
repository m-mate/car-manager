package com.example.frontend.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.frontend.CarApiService
import com.example.frontend.R
import com.example.frontend.RetrofitClient
import com.example.frontend.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        usernameInput = view.findViewById(R.id.etUsername)
        passwordInput = view.findViewById(R.id.etPassword)
        loginButton = view.findViewById(R.id.btnLogin)
        registerTextView = view.findViewById(R.id.tvRegister)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (validateLogin(username, password)) {
                val user = User(username, password) // Pass the email if needed

                // Make API call to verify login
                val apiService = RetrofitClient.retrofit.create(CarApiService::class.java)
                val call = apiService.loginUser(user)

                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            val token = response.body() // The JWT token returned from the server

                            if (!token.isNullOrEmpty()) {
                                // Handle successful login and save token
                                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                                // Store JWT token if necessary (e.g., SharedPreferences)
                                // Navigate to HomeFragment (or wherever needed)
                                findNavController().navigate(R.id.action_login_to_home)
                            } else {
                                Toast.makeText(requireContext(), "Empty token received", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Log response error code
                            Toast.makeText(requireContext(), "Invalid credentials or server error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        // Handle network failure
                        Log.e("LoginError", "Error: ${t.message}", t)
                        Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        registerTextView.setOnClickListener {
            // Navigate to RegisterFragment
            findNavController().navigate(R.id.action_login_to_register)
        }

        return view
    }

    private fun validateLogin(username: String, password: String): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Both fields are required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}

