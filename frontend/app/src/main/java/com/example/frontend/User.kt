package com.example.frontend

data class User(

    val username: String,
    val password: String = "",
    val email: String = "",
    val id: Int? = null,
    val role: String? = null
)
