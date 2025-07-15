package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.RefreshToken
import com.github.nenadjakic.useraccess.repository.RefreshTokenRepository
import com.github.nenadjakic.useraccess.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class RefreshTokenService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun findByTenantIdAndUsernameAndToken(tenantId: UUID, username: String, token: String): RefreshToken =
        refreshTokenRepository.findByUserTenantIdAndUserUsernameAndTokenAndExpireAtGreaterThanEqual(
            tenantId,
            username,
            token,
            OffsetDateTime.now()
        )
            ?: throw RuntimeException("Invalid username")

    fun create(tenantId: UUID, username: String): RefreshToken =
        (userRepository.findByUsernameAndTenantId(username, tenantId)
            .orElseThrow { EntityNotFoundException("User not found") })
            .let {
                refreshTokenRepository.save(RefreshToken(it))
            }
}