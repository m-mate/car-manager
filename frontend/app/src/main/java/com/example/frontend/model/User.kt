package com.example.frontend.model

data class User(

    val username: String,
    val password: String = "",
    val email: String = "",
    val id: Int? = null,
    val role: String? = null
)
