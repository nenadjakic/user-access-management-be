package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.Role
import com.github.nenadjakic.useraccess.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RoleService(
    private val roleRepository: RoleRepository
): CrudService<Role, UUID> {
    override fun findById(id: UUID): Role? = roleRepository.findById(id).orElse(null)

    override fun create(entity: Role): Role = roleRepository.save(entity)

    override fun update(entity: Role): Role {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Role) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun find(pageable: Pageable): Page<Role> = roleRepository.findAll(pageable)
}