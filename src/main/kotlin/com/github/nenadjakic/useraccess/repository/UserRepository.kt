package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsernameAndTenantId(username: String, tenantId: UUID): Optional<User>

    fun existsByUsernameAndTenantId(username: String, tenantId: UUID): Boolean

    fun findAllByTenantId(tenantId: UUID, pageable: Pageable): Page<User>
}