package com.github.nenadjakic.useraccess.controller

import com.github.nenadjakic.useraccess.annotation.CurrentTenantId
import com.github.nenadjakic.useraccess.security.filter.JwtAuthenticationFilter
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/info")
class InfoController {
    @GetMapping("/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun me(
        @Parameter(hidden = true)
        @CurrentTenantId tenantId: UUID
    ): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        val userDetails = auth.principal as? UserDetails
        val details = auth.details as? JwtAuthenticationFilter.AuthenticationDetails

        val claims = details?.claims
        val requestInfo = details?.requestDetails

        return ResponseEntity.ok(
            mapOf(
                "tenantId" to tenantId,
                "username" to userDetails?.username,
                "aud" to claims?.audience,
                "ip" to requestInfo?.remoteAddress,
                "sessionId" to requestInfo?.sessionId,
                "roles" to claims?.get("roles"),
            )
        )
    }
}