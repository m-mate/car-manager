package com.example.frontend.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.frontend.CarApiService
import com.example.frontend.R
import com.example.frontend.RetrofitClient
import com.example.frontend.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {/*
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view)


        bottomNavigationView.visibility = View.GONE

        return ComposeView(requireContext()).apply {
            setContent {
                RegisterScreen(
                    onRegister = { username, email, password ->
                        handleRegister(username, email, password)
                    },
                    onNavigateToLogin = {
                        findNavController().navigate(R.id.loginFragment)
                    }
                )
            }
        }
    }

    private fun handleRegister(username: String, email: String, password: String) {
        val user = User(username, password, email)
        val apiService = RetrofitClient.create("").create(CarApiService::class.java)

        apiService.registerUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.loginFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Registration failed! ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "An error occurred: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

*/
}
