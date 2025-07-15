package com.github.nenadjakic.useraccess

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.modelmapper.ModelMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
@EnableJpaAuditing
class Application {
    @Bean
    fun modelMapper(): ModelMapper =
        ModelMapper().also {

        }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    private fun createAPIKeyScheme(): SecurityScheme {
        return SecurityScheme().type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer")
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(SecurityRequirement().addList("Bearer_Authentication"))
            .components(Components().addSecuritySchemes("Bearer_Authentication", createAPIKeyScheme()))
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}