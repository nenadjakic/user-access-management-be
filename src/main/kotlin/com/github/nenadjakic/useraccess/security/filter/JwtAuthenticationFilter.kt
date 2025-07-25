package com.github.nenadjakic.useraccess.security.filter

import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import com.github.nenadjakic.useraccess.service.JwtService
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Custom JWT authentication filter.
 *
 * This filter is responsible for intercepting incoming HTTP requests and processing JWT authentication.
 */
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    data class AuthenticationDetails(
        val claims: Claims,
        val requestDetails: WebAuthenticationDetails
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response)
            return
        }
        val token = getAccessToken(request)

        if (!jwtService.isValid(token)) {
            filterChain.doFilter(request, response)
            return
        }

        setAuthenticationContext(token, request)
        filterChain.doFilter(request, response)
    }

    private fun setAuthenticationContext(token: String, request: HttpServletRequest) {
        val userDetails: UserDetails = getUserDetails(token)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val claims = jwtService.extractAllClaims(token)
        authentication.details = AuthenticationDetails(claims, WebAuthenticationDetailsSource().buildDetails(request))
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun hasAuthorizationBearer(request: HttpServletRequest): Boolean {
        val header = request.getHeader("Authorization")
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer")
    }

    private fun getAccessToken(request: HttpServletRequest): String {
        val header = request.getHeader("Authorization")
        return header.split(" ")[1].trim()
    }

    private fun getUserDetails(token: String): UserDetails {
        val userDetails = LocalUserDetails()
        val claims = jwtService.extractAllClaims(token)
        userDetails.username = claims[Claims.SUBJECT] as String
        val anyRoles = claims["roles"]
        if (anyRoles != null && anyRoles is List<*>) {
            anyRoles.filterIsInstance<String>().forEach { userDetails.addAuthority(SimpleGrantedAuthority(it)) }
        }

        return userDetails
    }
}