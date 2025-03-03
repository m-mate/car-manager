package com.example.frontend
import com.example.frontend.model.Car
import com.example.frontend.model.CarData
import com.example.frontend.model.CarDataLive
import com.example.frontend.model.Route
import com.example.frontend.model.RouteDetails
import com.example.frontend.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CarApiService {

    @GET("/server/ping")
    suspend fun pingServer(): Response<String>

    @GET("get/{vin}")
    suspend fun getCarStatus(@Path("vin") vin: String,  @Header("Authorization") token: String): Response<CarDataLive>

    @DELETE("/cars/{id}")
    suspend fun deleteCar(@Path("id") id: Int,  @Header("Authorization") token: String): Response<Unit>

    @GET("/routes/{routeId}/details")
    suspend fun getRouteDetails(@Path("routeId") routeId: Int, @Header("Authorization") token: String): RouteDetails


    @POST("/users/login")
    fun loginUser(@Body user: User): Call<String>

    @GET("cars/user/{username}")
    suspend fun getCarsByUser(@Path("username") username: String,  @Header("Authorization") token: String): Response<List<Car>?>

    @POST("cars/save/{username}")
    suspend fun saveCar(@Path("username") username: String, @Body car: Car, @Header("Authorization") token: String): Response<Void>

    @GET("/routes/all")
    suspend fun getAllRoutes(@Query("username") username: String, @Query("carId") carId: Int, @Header("Authorization") token: String): List<Route>

    @POST("users/register")
    suspend fun registerUser(@Body user: User): Response<User>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String,@Header("Authorization") token: String): Response<User>


    @PUT("users/{username}")
    suspend fun updateUser(@Path("username") username: String, @Body user: User, @Header("Authorization") token: String): Response<User>

    @GET("/users/all")
    suspend fun getAllUsers(@Header("Authorization") token: String): Response<List<User>>

    @DELETE("users/delete/{id}")
    suspend fun deleteUser(@Path("id") userId: Int, @Header("Authorization") token: String): Response<Void>

    @PUT("users/change-role/{id}")
    suspend fun changeUserRole(@Path("id") userId: Int, @Header("Authorization") token: String): Response<User>

    @POST("routes/check/{username}/{carId}")
    suspend fun refreshRoutes(@Path("username") username: String, @Path("carId") carId: Int, @Header("Authorization") token: String): Response<Unit>

    @DELETE("routes/delete/{routeId}")
    suspend fun deleteRoute(@Path("routeId") routeId: Int, @Header("Authorization") token: String): Response<Void>

}