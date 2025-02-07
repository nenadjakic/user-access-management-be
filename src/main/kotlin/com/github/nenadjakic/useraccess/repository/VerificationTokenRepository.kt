package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.VerificationToken
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface VerificationTokenRepository : JpaRepository<VerificationToken, UUID> {

    @EntityGraph(attributePaths = ["user"])
    fun findByToken(token: String): VerificationToken?
}