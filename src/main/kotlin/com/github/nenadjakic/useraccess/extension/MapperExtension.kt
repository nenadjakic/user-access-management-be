package com.github.nenadjakic.useraccess.extension

import com.github.nenadjakic.useraccess.dto.RegisterRequest
import com.github.nenadjakic.useraccess.entity.User
import com.github.nenadjakic.useraccess.repository.ClientRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

fun RegisterRequest.toUser(
    passwordEncoder: PasswordEncoder,
    clientRepository: ClientRepository
): User {
    return User().also {
        it.enabled = false
        it.emailConfirmed = false
        it.username = this.email
        it.email = this.email
        it.password = passwordEncoder.encode(this.password)
        it.client = clientRepository.findByName(this.clientId).orElseThrow { EntityNotFoundException("Client does not exists") }
    }
}