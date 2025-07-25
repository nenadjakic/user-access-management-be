package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.dto.RoleRequest
import com.github.nenadjakic.useraccess.dto.RoleResponse
import com.github.nenadjakic.useraccess.repository.RoleRepository
import com.github.nenadjakic.useraccess.repository.TenantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val tenantRepository: TenantRepository
) {
    fun findById(tenantId: UUID, id: UUID): RoleResponse? =
        roleRepository
            .findByTenantIdAndId(tenantId, id)
            .map { RoleResponse(it) }
            .getOrNull()

    fun create(tenantId: UUID, request: RoleRequest): RoleResponse =
        request.toRole().also {
            it.tenant = tenantRepository.getReferenceById(tenantId)
        }.let {
            RoleResponse(roleRepository.save(it))
        }

    fun find(tenantId: UUID, pageable: Pageable): Page<RoleResponse> =
        roleRepository.findAllByTenantId(tenantId, pageable).map { RoleResponse(it) }
}