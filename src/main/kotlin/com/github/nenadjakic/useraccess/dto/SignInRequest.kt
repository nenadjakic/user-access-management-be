package com.github.nenadjakic.useraccess.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "Request body for user sign-in")
class SignInRequest {
    enum class GrantType {
        PASSWORD,
        REFRESH_TOKEN
    }

    @Schema(description = "Unique client identifier", example = "client-123")
    @NotNull(message = "Client ID must not be empty")
    var clientId: UUID? = null

    @Schema(description = "Username or email address", example = "user@example.com")
    @NotEmpty(message = "Username must not be empty")
    lateinit var username: String

    @Schema(description = "Password or refresh token", example = "StrongP@ssw0rd or eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotEmpty(message = "Password or refresh token must not be empty")
    lateinit var passwordOrRefreshToken: String

    @Schema(description = "Type of grant", example = "PASSWORD")
    @NotNull(message = "Grant type must not be null")
    var grantType: GrantType? = null
}