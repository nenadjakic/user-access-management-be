package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.Tenant
import com.github.nenadjakic.useraccess.repository.TenantRepository
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val tenantRepository: TenantRepository
) {
    fun findAll(): List<Tenant> =
        tenantRepository.findAll()
}