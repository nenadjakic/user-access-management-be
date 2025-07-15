package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.Permission
import com.github.nenadjakic.useraccess.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

class PermissionService(
    private val permissionRepository: PermissionRepository
) {

    fun findById(id: UUID): Permission =
        permissionRepository
            .findById(id)
            .orElseThrow {
                IllegalArgumentException("Permission with id $id not found")
            }

    fun findAll(tenantId: UUID, pageable: Pageable): Page<Permission> =
        permissionRepository.findAllByTenantId(tenantId, pageable)

    fun create(entity: Permission): Permission =
        permissionRepository.save(entity)

    fun update(entity: Permission): Permission =
        permissionRepository.save(entity)

    fun delete(entity: Permission) =
        permissionRepository.delete(entity)

    fun deleteById(id: UUID) =
        permissionRepository.deleteById(id)
}