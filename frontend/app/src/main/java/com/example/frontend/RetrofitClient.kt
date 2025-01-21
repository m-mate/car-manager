package com.example.frontend

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080"

    // Create a Gson instance with lenient parsing enabled
    private val gson = GsonBuilder()
        .setLenient() // Allows lenient parsing of malformed JSON
        .create()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val carApiService: CarApiService = retrofit.create(CarApiService::class.java)
}