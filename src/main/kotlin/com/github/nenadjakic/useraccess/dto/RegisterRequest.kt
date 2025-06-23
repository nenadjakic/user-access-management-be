package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.validation.PasswordMatches
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@PasswordMatches(message = "Passwords do not match")
class RegisterRequest: ConfirmPassword {

    @Schema(description = "Unique client identifier", example = "client-123")
    @NotEmpty(message = "Client ID must not be empty")
    lateinit var clientId: String

    @Schema(description = "User email address", example = "user@example.com")
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email must not be empty")
    lateinit var email: String

    @Schema(description = "User password", example = "StrongP@ssw0rd")
    @NotEmpty(message = "Password must not be empty")
    override lateinit var password: String

    @Schema(description = "Confirmation of the password", example = "StrongP@ssw0rd")
    @NotEmpty(message = "Confirmed password must not be empty")
    override lateinit var confirmedPassword: String
}