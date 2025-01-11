package com.pimsmart.V1.Service.ServiceIml.Admin

import com.pimsmart.V1.Service.Admin.LoginService
import com.pimsmart.V1.config.TokenGenerator
import com.pimsmart.V1.entities.Admin.LoginEntity
import com.pimsmart.V1.repository.Admin.LoginRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service

class LoginServiceIml (
    private val loginRepository: LoginRepository,
    private val passwordEncoder: PasswordEncoder, // Inject PasswordEncoder
    private val tokenGenerator: TokenGenerator // Make sure to inject TokenGenerator

    ): LoginService {


    override fun register(email: String, password: String): LoginEntity {
        if (loginRepository.existsByEmail(email)) {
            throw Exception("Email already exists")
        }

        // Encode the password
        val hashedPassword = passwordEncoder.encode(password)

        // Create new user
        val newUser = LoginEntity(email = email, password = hashedPassword)

        // Save the user in the database
        val savedUser = loginRepository.save(newUser)

        // Generate JWT token for the new user
        val token = tokenGenerator.generateToken(savedUser) // Use tokenGenerator to create the token
        savedUser.token = token // Set the token to the saved user
        println("Generated Token: $token") // Log the generated token

        // Save the user again to update the token in the database
        return loginRepository.save(savedUser) // Ensure the updated user is saved
    }


    override fun login(email: String, password: String): String {
        // Check if the user exists by email
        val user = loginRepository.findByEmail(email) ?: throw Exception("User not found")

        // Validate the password
        if (!passwordEncoder.matches(password, user.password)) {
            throw Exception("Invalid password")
        }

        // Return the existing token from the database
        return user.token ?: throw Exception("Token not found")
    }
}