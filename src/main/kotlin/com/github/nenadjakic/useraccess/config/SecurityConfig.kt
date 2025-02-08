package com.github.nenadjakic.useraccess.config

import com.github.nenadjakic.useraccess.security.LocalUserDetailsService
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig() {
}