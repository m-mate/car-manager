package com.example.frontend.ui.dashboard



import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.frontend.CarApiService
import com.example.frontend.CarData
import com.example.frontend.RetrofitClient
import com.example.frontend.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var handler: Handler
    private val updateRunnable: Runnable = object : Runnable {
        override fun run() {
            getCarStatus() // Call your method to fetch car status
            handler.postDelayed(this, 1000) // Repeat every 1000 ms (1 second)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Start the handler to fetch car status
        handler = Handler(Looper.getMainLooper())
        handler.post(updateRunnable)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable) // Stop the updates when the view is destroyed
        _binding = null
    }

    private fun getCarStatus() {
        // Retrieve JWT token from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        // Dynamically create the CarApiService using RetrofitClient
        val apiService = RetrofitClient.create(token).create(CarApiService::class.java)

        apiService.getCarStatus().enqueue(object : Callback<CarData> {
            override fun onResponse(call: Call<CarData>, response: Response<CarData>) {
                if (response.isSuccessful) {
                    val carData = response.body()
                    // Update UI with the data
                    carData?.let {
                        binding.speedometer.speedTo(it.speed.toFloat())
                        binding.rpmMeter.speedTo(it.rpm.toFloat())
                        binding.fuelMeter.speedTo(it.fuelLevel.toFloat())
                    }
                } else {
                    // Handle the error response
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CarData>, t: Throwable) {
                // Show an error message to the user
                Toast.makeText(requireContext(), "Failed to fetch data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


