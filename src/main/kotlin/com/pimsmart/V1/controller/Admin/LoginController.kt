package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.LoginService
import com.pimsmart.V1.Service.Admin.RegisterAdminService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.TokenGenerator
import com.pimsmart.V1.dto.LoginRequest
import com.pimsmart.V1.dto.RegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/Admin")

class LoginController(
    private val tokenGenerator: TokenGenerator,
    private val loginService: LoginService,

    ) {
    @PostMapping("/Login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<Map<String, String>>> {
        return try {
            val token = loginService.login(request.email, request.password)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = mapOf("token" to token),
                    message = "Login successful"
                )
            )
        } catch (e: Exception) {
            // Set a specific message based on the exception type or message
            val message = when (e.message) {
                "User not found" -> "User not found"
                "Invalid password" -> "Invalid password provided"
                else -> "Login failed due to server error"
            }

            ResponseEntity.badRequest().body(
                ApiResponse<Map<String, String>>(
                    success = false,
                    data = null,
                    message = message, // Use specific error message
                    error = e.message
                )
            )
        }
    }


    @PostMapping("/Register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            // Call the service to register the user
            val user = loginService.register(request.email, request.password)

            // Generate the token for the registered user
            val token = tokenGenerator.generateToken(user)

            // Return success response with the generated token
            ResponseEntity.ok(mapOf("message" to "User registered successfully", "token" to token))
        } catch (e: Exception) {
            // Return error response if any issue occurs
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}