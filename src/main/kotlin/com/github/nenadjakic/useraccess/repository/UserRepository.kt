package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}