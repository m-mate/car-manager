package com.example.frontend
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {

    @GET("/get/vin1")
    fun getCarStatus(): Call<CarData>

    @POST("/api/car/status")
    fun sendCarStatus(@Body carData: CarData): Call<String>

    @POST("/users/register")
    fun registerUser(@Body user: User): Call<User>



    @POST("/users/login")
    fun loginUser(@Body user: User): Call<String>

    @GET("cars/user/{username}")
    fun getCarsByUser(@Path("username") username: String): Call<List<Car>>


}