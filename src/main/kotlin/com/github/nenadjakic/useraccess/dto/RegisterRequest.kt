package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.validation.PasswordMatches
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

@PasswordMatches
class RegisterRequest: ConfirmPassword {

    @Email
    @NotEmpty
    lateinit var email: String

    @NotEmpty
    override lateinit var password: String

    @NotEmpty
    override lateinit var confirmedPassword: String
}