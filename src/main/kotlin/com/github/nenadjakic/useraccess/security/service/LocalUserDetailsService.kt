package com.github.nenadjakic.useraccess.security.service

import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import com.github.nenadjakic.useraccess.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LocalUserDetailsService(
    private val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null || !username.contains("|")) {
            throw UsernameNotFoundException("User not found.")
        }
        val (tenantIdAsString, realUsername) = username.split("|", limit = 2)
        val tenantId = UUID.fromString(tenantIdAsString)

        val user = userService.findByUsernameAndTenantId(realUsername, tenantId)
            .orElseThrow { UsernameNotFoundException("User not found.") }

        return LocalUserDetails(user)
    }
}