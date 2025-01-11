package com.pimsmart.V1.config

import com.pimsmart.V1.entities.Admin.LoginEntity
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class TokenGenerator {

    private val secretKey: String = generateSecretKey()

    private fun generateSecretKey(): String {
        val random = SecureRandom()
        val bytes = ByteArray(24) // 24 bytes = 192 bits
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes) // Encode to Base64 string
    }

    fun generateToken(user: LoginEntity): String {
        return Jwts.builder()
            .setSubject(user.email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 86400000)) // Set token expiration
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, secretKey) // Use the generated secret key
            .compact()
    }
}
