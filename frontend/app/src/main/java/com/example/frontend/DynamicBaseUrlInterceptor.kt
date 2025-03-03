package com.example.frontend

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class DynamicBaseUrlInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        // Retrieve the latest base URL from SharedPreferences
        val serverAddress = sharedPreferences.getString("server_address", "") ?: ""
        val urlWithoutScheme = serverAddress.replace("http://", "").replace("https://", "")
        val host = urlWithoutScheme.substringBefore(":") // Extract only the host
        val port = urlWithoutScheme.substringAfter(":", "8080").toIntOrNull() ?: 8080 // Extract port safely
        if (serverAddress.isBlank()) {
            return chain.proceed(originalRequest) // Use existing URL if no server set
        }

        val newUrl = originalUrl.newBuilder()
            .scheme("http") // Change to "https" if needed
            .host(host) // ✅ Use only the host
            .port(port)  // Update only the host (keeps path/query)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
