package com.example.frontend
import com.example.frontend.model.Route
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CarApiService {

    @GET("/get/vin1")
    fun getCarStatus(): Call<CarData>

    @POST("/api/car/status")
    fun sendCarStatus(@Body carData: CarData): Call<String>





    @POST("/users/login")
    fun loginUser(@Body user: User): Call<String>

    @GET("cars/user/{username}")
    fun getCarsByUser(@Path("username") username: String): Call<List<Car>>

    @POST("cars/save/{username}")
    fun saveCar(@Path("username") username: String, @Body car: Car): Call<Void>

    @GET("/all")
    fun getAllRoutes(@Query("username") username: String, @Query("carId") carId: Int): Call<List<Route>>

    @POST("/register")
    fun registerUser(@Body user: User): Call<User>
}