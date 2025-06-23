package com.github.nenadjakic.useraccess.security

import com.github.nenadjakic.useraccess.repository.UserRepository
import com.github.nenadjakic.useraccess.security.model.ClientUsernameAuthenticationToken
import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import com.github.nenadjakic.useraccess.service.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class ClientUsernameAuthenticationProvider(
    private val userRepository: UserRepository
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val token = authentication as ClientUsernameAuthenticationToken
        val user = userRepository.findByUsernameAndClientName(token.username, token.clientId)
            ?: throw UsernameNotFoundException("User not found.")
        return ClientUsernameAuthenticationToken(token.clientId, token.username, token.credentials).apply {
            this.details = LocalUserDetails(user)
        }
    }

    override fun supports(authentication: Class<*>): Boolean =
        ClientUsernameAuthenticationToken::class.java.isAssignableFrom(authentication)
}