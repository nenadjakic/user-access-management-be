package com.github.nenadjakic.useraccess.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class ForgotPasswordRequest(
    @NotEmpty
    @Email
    val username: String
)
