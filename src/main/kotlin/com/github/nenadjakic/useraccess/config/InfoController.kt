package com.github.nenadjakic.useraccess.config

import com.github.nenadjakic.useraccess.security.filter.JwtAuthenticationFilter
import io.jsonwebtoken.Claims
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/info")
class InfoController {
    @GetMapping("/me")
    fun me(): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        val userDetails = auth.principal as? UserDetails
        val details = auth.details as? JwtAuthenticationFilter.AuthenticationDetails

        val claims = details?.claims
        val requestInfo = details?.requestDetails

        return ResponseEntity.ok(
            mapOf(
                "username" to userDetails?.username,
                "aud" to claims?.audience,
                "ip" to requestInfo?.remoteAddress,
                "sessionId" to requestInfo?.sessionId,
                "roles" to claims?.get("roles"),
            )
        )
    }

}