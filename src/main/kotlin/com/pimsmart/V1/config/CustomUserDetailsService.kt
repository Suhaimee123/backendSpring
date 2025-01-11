package com.pimsmart.V1.config


import com.pimsmart.V1.entities.Admin.LoginEntity
import com.pimsmart.V1.repository.Admin.LoginRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val loginRepository: LoginRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user: LoginEntity = loginRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found")
        return org.springframework.security.core.userdetails.User(user.email, user.password, emptyList())
    }
}