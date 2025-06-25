package com.github.nenadjakic.useraccess.security.model

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class ClientUsernameAuthenticationToken(
    val clientId: String,
    val username: String,
    credentials: Any?
) : UsernamePasswordAuthenticationToken(username, credentials)