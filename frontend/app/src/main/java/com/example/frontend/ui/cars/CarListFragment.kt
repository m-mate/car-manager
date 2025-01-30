package com.example.frontend.ui.cars

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateListOf
import com.example.frontend.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.Car
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarListFragment : Fragment() {/*

    private lateinit var bottomNavigationView: BottomNavigationView
    private var carList = mutableStateListOf<Car>() // Use mutableStateListOf for Compose

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Find the BottomNavigationView in the activity's layout
        bottomNavigationView = requireActivity().findViewById(R.id.nav_view)

        // Hide the Bottom Navigation in CarListFragment
        bottomNavigationView.visibility = View.GONE  // Hide
        fetchCarsForUser()
        // Return ComposeView with CarListScreen composable
        return ComposeView(requireContext()).apply {
            setContent {
                CarListScreen(
                    carList = carList,
                    onAddCarClicked = {
                        // Handle "Add Car" action (e.g., navigate to add car screen)
                        Toast.makeText(requireContext(), "Navigate to Add Car screen", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), AddCarActivity::class.java)
                        startActivity(intent)
                    },
                    onCarClicked = { car ->
                        Toast.makeText(requireContext(), "Car clicked: ${car.type} - VIN: ${car.vin}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun fetchCarsForUser() {
        // Retrieve JWT token from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }
        val username =  sharedPreferences.getString("username", null)

        if (username.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }
        // Use RetrofitClient with the token
        val apiService = RetrofitClient.create(token).create(CarApiService::class.java)

        // Make API call to fetch cars for the user
        apiService.getCarsByUser(username) // Replace "admin" with dynamic username if needed
            .enqueue(object : Callback<List<Car>> {
                override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val cars = response.body()!!

                        // Update the mutableStateListOf with the fetched car list
                        carList.clear()  // Clear existing data
                        carList.addAll(cars)  // Add new cars

                        Log.d("CarListFragment", "Fetched cars: $cars")
                    } else {
                        Toast.makeText(requireContext(), "No cars found or server error.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                    // Handle network or API call failure
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Optionally, show the BottomNavigationView again when navigating away from LoginFragment
        bottomNavigationView.visibility = View.VISIBLE
    }*/
}