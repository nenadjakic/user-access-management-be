package com.github.nenadjakic.useraccess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

class PasswordChangeRequest : ConfirmPassword {
    @NotBlank
    lateinit var currentPassword: String

    @JsonProperty("newPassword")
    @NotBlank
    override lateinit var password: String

    @NotBlank
    override lateinit var confirmedPassword: String
}