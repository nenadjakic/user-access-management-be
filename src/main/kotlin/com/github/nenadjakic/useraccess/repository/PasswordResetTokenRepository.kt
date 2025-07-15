package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.PasswordResetToken
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, UUID> {
    @EntityGraph(attributePaths = ["user"])
    fun findByToken(token: String): Optional<PasswordResetToken>
}