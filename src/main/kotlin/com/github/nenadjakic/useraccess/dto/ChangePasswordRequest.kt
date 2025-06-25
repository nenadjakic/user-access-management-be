package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.validation.PasswordMatches
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

@Schema(description = "Request payload for changing a user's password")
@PasswordMatches(message = "Passwords do not match")
class ChangePasswordRequest: ConfirmPassword {

    @Schema(description = "Unique client identifier", example = "client-123")
    @NotEmpty(message = "Client ID must not be empty")
    lateinit var clientId: String

    @Schema(description = "Username")
    @NotEmpty(message = "Username must not be empty")
    lateinit var username: String

    @Schema(description = "Current password of the user", example = "oldPassword123")
    @NotEmpty(message = "Current password must not be empty")
    lateinit var currentPassword: String

    @Schema(description = "New password to set", name = "newPassword", example = "newPassword456")
    @NotEmpty(message = "New password must not be empty")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    override lateinit var password: String

    @Schema(description = "Confirmation of the new password", example = "newPassword456")
    @NotEmpty(message = "Confirmed password must not be empty")
    override lateinit var confirmedPassword: String
}