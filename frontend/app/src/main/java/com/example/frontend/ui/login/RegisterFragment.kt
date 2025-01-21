package com.example.frontend.ui.login

import android.os.Bundle
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

class RegisterFragment : Fragment() {

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        usernameInput = view.findViewById(R.id.etUsername)
        emailInput = view.findViewById(R.id.etEmail)
        passwordInput = view.findViewById(R.id.etPassword)
        confirmPasswordInput = view.findViewById(R.id.etConfirmPassword)
        registerButton = view.findViewById(R.id.btnRegister)


        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()


            if (validateInputs(username, email, password, confirmPassword)) {
                val user = User(username, password, email)

                // Make API call to register the user
                val apiService = RetrofitClient.retrofit.create(CarApiService::class.java)
                val call = apiService.registerUser(user)

                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            // Handle successful registration
                            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                            // Navigate to the LoginFragment
                            findNavController().navigate(R.id.loginFragment)
                        } else {
                            // Handle registration failure (e.g., username already exists)
                            Toast.makeText(requireContext(), "Registration failed!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        // Handle failure (e.g., network issues)
                        Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        val tvLogin = view.findViewById<TextView>(R.id.tvLogin)
        tvLogin.setOnClickListener {
            // Navigate back to the LoginFragment
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }

    private fun validateInputs(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
