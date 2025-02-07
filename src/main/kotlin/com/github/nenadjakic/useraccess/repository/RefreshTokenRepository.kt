package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.RefreshToken
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    @EntityGraph(attributePaths = ["user"])
    fun findByUserUsernameAndTokenAndExpireAtGreaterThanEqual(username: String, token: String, now: OffsetDateTime): RefreshToken?
}