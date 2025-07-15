package com.github.nenadjakic.useraccess.annotation

import com.github.nenadjakic.useraccess.security.filter.JwtAuthenticationFilter
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID

class CurrentTenantIdResolver: HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentTenantId::class.java) &&
                parameter.parameterType == UUID::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication context found")

        val details = authentication.details as? JwtAuthenticationFilter.AuthenticationDetails
            ?: throw IllegalStateException("Authentication details not found")

        val aud = (details.claims["aud"] as? Collection<*>)?.firstOrNull() as? String
            ?: throw IllegalStateException("No 'aud' claim found in JWT")

        return UUID.fromString(aud)
    }
}