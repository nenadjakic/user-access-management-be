package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.RefreshToken
import com.github.nenadjakic.useraccess.repository.RefreshTokenRepository
import com.github.nenadjakic.useraccess.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class RefreshTokenService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun findByClientIdAndUsernameAndToken(clientId: String, username: String, token: String): RefreshToken =
        refreshTokenRepository.findByUserClientNameAndUserUsernameAndTokenAndExpireAtGreaterThanEqual(
            clientId,
            username,
            token,
            OffsetDateTime.now()
        )
            ?: throw RuntimeException("Invalid username")

    fun create(username: String): RefreshToken =
        (userRepository.findByUsername(username)
            ?: throw EntityNotFoundException("User not found"))
            .let {
                refreshTokenRepository.save(RefreshToken(it))
            }
}