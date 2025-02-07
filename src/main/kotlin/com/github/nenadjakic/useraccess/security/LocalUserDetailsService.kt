package com.github.nenadjakic.useraccess.security

import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import com.github.nenadjakic.useraccess.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class LocalUserDetailsService(
    private val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException("User not found.")
        }
        return LocalUserDetails(userService.findByUsername(username) ?: throw UsernameNotFoundException("User not found."))
    }
}