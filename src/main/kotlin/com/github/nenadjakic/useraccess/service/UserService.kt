package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.config.UserAccessManagementProperties
import com.github.nenadjakic.useraccess.entity.User
import com.github.nenadjakic.useraccess.repository.*
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val userAccessManagementProperties: UserAccessManagementProperties,
    private val passwordEncoder: PasswordEncoder,
    private val tenantRepository: TenantRepository
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun findByUsernameAndTenantId(username: String, tenantId: UUID): Optional<User> {
        return userRepository.findByUsernameAndTenantId(username, tenantId)
    }

    fun getAllUsers(tenantId: UUID, pageable: Pageable): Page<User> =
        userRepository.findAllByTenantId(tenantId, pageable)

    fun getById(id: UUID): User = userRepository.findById(id).orElseThrow { throw EntityNotFoundException("User not found") }

    fun unlockUser(id: UUID): User =
        userRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("User not found") }
            .apply { locked = false }
            .let { userRepository.save(it) }

    fun disableUser(id: UUID): User =
        userRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("User not found") }
            .apply { enabled = false }
            .let { userRepository.save(it) }

    fun addRole(id: UUID, roleId: UUID) {
        userRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("User not found") }
            .apply { addRole(roleRepository.getReferenceById(roleId)) }
            .let { userRepository.save(it) }
    }

    fun removeRole(id: UUID, roleId: UUID) {
        userRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("User not found") }
            .apply { removeRoleById(roleId) }
            .let { userRepository.save(it) }
    }
}