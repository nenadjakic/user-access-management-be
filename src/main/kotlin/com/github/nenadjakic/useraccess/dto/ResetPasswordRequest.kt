package com.github.nenadjakic.useraccess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.nenadjakic.useraccess.validation.PasswordMatches
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty


@Schema(description = "Request body for resetting a user's password")
@PasswordMatches(message = "Passwords do not match")
data class ResetPasswordRequest(
    @Schema(
        description = "Password reset token sent to the user's email",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    )
    @NotEmpty(message = "Token must not be empty")
    val token: String,

    @Schema(
        description = "New password to set",
        example = "StrongP@ssw0rd"
    )
    @NotEmpty(message = "New password must not be empty")
    @JsonProperty("newPassword")
    override var password: String,

    @Schema(
        description = "Confirmation of the new password",
        example = "StrongP@ssw0rd"
    )
    @NotEmpty(message = "Confirmed password must not be empty")
    override var confirmedPassword: String
): ConfirmPassword
