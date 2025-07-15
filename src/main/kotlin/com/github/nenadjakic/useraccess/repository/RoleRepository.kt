package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.Permission
import com.github.nenadjakic.useraccess.entity.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID> {

    fun findByTenantIdAndId(tenantId: UUID, id: UUID): Optional<Role>
    fun findAllByTenantId(tenantId: UUID, pageable: Pageable): Page<Role>
}