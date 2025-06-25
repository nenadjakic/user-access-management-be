package com.github.nenadjakic.useraccess.dto.converter

import com.github.nenadjakic.useraccess.dto.RegisterRequest
import com.github.nenadjakic.useraccess.entity.User
import com.github.nenadjakic.useraccess.repository.ClientRepository
import jakarta.persistence.EntityNotFoundException
import org.modelmapper.AbstractConverter
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterRequestToUserConverter(
    private val passwordEncoder: PasswordEncoder,
    private val clientRepository: ClientRepository
) : AbstractConverter<RegisterRequest, User>() {
    override fun convert(source: RegisterRequest?): User {
        return User().apply {
            enabled = false
            emailConfirmed = false
            username = source!!.email
            email = source.email
            password = passwordEncoder.encode(source.password)
            client = clientRepository.findByName(source.clientId).orElseThrow { EntityNotFoundException("Client does not exists") }
        }
    }
}