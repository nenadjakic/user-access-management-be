package com.github.nenadjakic.useraccess.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

class SignInRequest {
    enum class GrantType {
        PASSWORD,
        REFRESH_TOKEN
    }

    @NotEmpty
    lateinit var username: String

    @NotEmpty
    lateinit var passwordOrRefreshToken: String

    @NotNull
    lateinit var grantType: GrantType
}