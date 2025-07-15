package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface TenantRepository: JpaRepository<Tenant, UUID> {
    fun findByName(name: String): Optional<Tenant>
}