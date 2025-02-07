package com.github.nenadjakic.useraccess.dto.converter

import com.github.nenadjakic.useraccess.dto.RegisterRequest
import com.github.nenadjakic.useraccess.entity.User
import org.modelmapper.AbstractConverter
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterRequestToUserConverter(
    private val passwordEncoder: PasswordEncoder
) : AbstractConverter<RegisterRequest, User>() {
    override fun convert(source: RegisterRequest?): User {
        return User().apply {
            enabled = false
            emailConfirmed = false
            username = source!!.email
            email = source.email
            password = passwordEncoder.encode(source.password)
        }
    }
}