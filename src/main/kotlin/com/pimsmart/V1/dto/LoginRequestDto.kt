package com.pimsmart.V1.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)


data class RegisterRequest(
    val email: String,
    val password: String
)
