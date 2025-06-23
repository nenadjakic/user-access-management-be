package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.RefreshToken
import com.github.nenadjakic.useraccess.repository.RefreshTokenRepository
import com.github.nenadjakic.useraccess.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class RefreshTokenService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun findByUsernameAndToken(clientId: String, username: String, token: String): RefreshToken {
        return refreshTokenRepository.findByUserClientNameAndUserUsernameAndTokenAndExpireAtGreaterThanEqual(clientId,username, token, OffsetDateTime.now())
            ?: throw RuntimeException("Invalid username")
    }

    fun create(username: String): RefreshToken? {
        return userRepository.findByUsername(username)?.let {
            refreshTokenRepository.save(RefreshToken(it))
        }
    }
}