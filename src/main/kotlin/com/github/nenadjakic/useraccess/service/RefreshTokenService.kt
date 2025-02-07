package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.RefreshToken
import com.github.nenadjakic.useraccess.repository.RefreshTokenRepository
import com.github.nenadjakic.useraccess.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
open class RefreshTokenService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    open fun findByUsernameAndToken(username: String, token: String): RefreshToken {
        return refreshTokenRepository.findByUserUsernameAndTokenAndExpireAtGreaterThanEqual(username, token, OffsetDateTime.now())
            ?: throw RuntimeException("Invalid username")
    }

    open fun create(username: String): RefreshToken? {
        return userRepository.findByUsername(username)?.let {
            refreshTokenRepository.save(RefreshToken(it))
        }
    }
}