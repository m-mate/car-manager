package com.example.frontend
import com.example.frontend.model.CarDataLive
import com.example.frontend.model.Route
import com.example.frontend.model.RouteDetails
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CarApiService {

    @GET("/server/ping")
    fun pingServer(): Call<String>

    @GET("get/{vin}")
    fun getCarStatus(@Path("vin") vin: String): Call<CarDataLive>

    @POST("/api/car/status")
    fun sendCarStatus(@Body carData: CarData): Call<String>

    @DELETE("/cars/{id}")
    fun deleteCar(@Path("id") id: Int): Call<Void>

    @GET("/routes/{routeId}/details")
    fun getRouteDetails(@Path("routeId") routeId: Int): Call<RouteDetails>


    @POST("/users/login")
    fun loginUser(@Body user: User): Call<String>

    @GET("cars/user/{username}")
    fun getCarsByUser(@Path("username") username: String): Call<List<Car>>

    @POST("cars/save/{username}")
    fun saveCar(@Path("username") username: String, @Body car: Car): Call<Void>

    @GET("/routes/all")
    fun getAllRoutes(@Query("username") username: String, @Query("carId") carId: Int): Call<List<Route>>

    @POST("users/register")
    fun registerUser(@Body user: User): Call<User>

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<User>


    @PUT("users/{username}")
    fun updateUser(@Path("username") username: String, @Body user: User): Call<User>

    @GET("/users/all")
    fun getAllUsers(): Call<List<User>>

    @DELETE("users/delete/{id}")
    fun deleteUser(@Path("id") userId: Int): Call<Void>

    @PUT("users/change-role/{id}")
    fun changeUserRole(@Path("id") userId: Int): Call<User>

    @POST("routes/check/{username}/{carId}")
    fun refreshRoutes(@Path("username") username: String, @Path("carId") carId: Int): Call<Void>

    @DELETE("routes/delete/{routeId}")
    fun deleteRoute(@Path("routeId") routeId: Int): Call<Void>

}