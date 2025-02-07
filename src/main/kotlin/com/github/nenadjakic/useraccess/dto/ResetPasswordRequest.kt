package com.github.nenadjakic.useraccess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.nenadjakic.useraccess.validation.PasswordMatches
import jakarta.validation.constraints.NotEmpty

@PasswordMatches
data class ResetPasswordRequest(
    @NotEmpty
    val token: String,

    @NotEmpty
    @JsonProperty("newPassword")
    override var password: String,

    @NotEmpty
    override var confirmedPassword: String
): ConfirmPassword
