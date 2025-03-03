package com.example.frontend

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun create(context: Context, token: String): Retrofit {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val baseUrl = sharedPreferences.getString("server_address", "192.168.100.55:8080") // Default fallback


        val client = OkHttpClient.Builder()
            .addInterceptor(DynamicBaseUrlInterceptor(sharedPreferences))
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("http://$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    fun reset() {
        retrofit = null // Forces Retrofit to be recreated with new settings
    }
}
