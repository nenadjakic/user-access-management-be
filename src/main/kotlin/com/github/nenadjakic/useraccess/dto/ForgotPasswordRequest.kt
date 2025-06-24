package com.github.nenadjakic.useraccess.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Schema(description = "Request body for initiating a password reset")
data class ForgotPasswordRequest(

    @param:Schema(
        description = "Unique client identifier",
        example = "client-123"
    )
    @param:NotEmpty(message = "Client ID must not be empty")
    val clientId: String,

    @param:Schema(
        description = "Username or email address for password reset",
        example = "user@example.com"
    )
    @param:Email(message = "Username must be a valid email address")
    @param:NotEmpty(message = "Username must not be empty")
    val username: String
)
