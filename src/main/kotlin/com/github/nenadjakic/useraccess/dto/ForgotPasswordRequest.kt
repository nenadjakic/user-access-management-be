package com.github.nenadjakic.useraccess.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Schema(description = "Request body for initiating a password reset")
data class ForgotPasswordRequest(
    @Schema(
        description = "Username or email address for password reset",
        example = "user@example.com"
    )

    @Email(message = "Username must be a valid email address")
    @NotEmpty(message = "Username must not be empty")
    val username: String
)
