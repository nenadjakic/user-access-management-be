package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.Permission
import com.github.nenadjakic.useraccess.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

class PermissionService(private val permissionRepository: PermissionRepository) : CrudService<Permission, UUID> {

    override fun findById(id: UUID): Permission? = permissionRepository.findById(id).orElse(null)

    override fun find(pageable: Pageable): Page<Permission> = permissionRepository.findAll(pageable)

    override fun create(entity: Permission): Permission = permissionRepository.save(entity)

    override fun update(entity: Permission): Permission = permissionRepository.save(entity)

    override fun delete(entity: Permission) = permissionRepository.delete(entity)

    override fun deleteById(id: UUID) = permissionRepository.deleteById(id)
}