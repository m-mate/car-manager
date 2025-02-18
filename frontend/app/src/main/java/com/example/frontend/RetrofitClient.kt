package com.example.frontend

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun create(context: Context, token: String): Retrofit {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val baseUrl = sharedPreferences.getString("server_address", "http://192.168.100.50:8080") // Default fallback

        if (baseUrl != null) {
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                throw IllegalArgumentException("Invalid base URL: $baseUrl. Ensure it starts with 'http://' or 'https://'.")
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl ?: "http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
}
