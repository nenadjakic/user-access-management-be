package com.github.nenadjakic.useraccess.security.service

import com.github.nenadjakic.useraccess.config.UserAccessManagementProperties
import com.github.nenadjakic.useraccess.dto.ChangePasswordRequest
import com.github.nenadjakic.useraccess.dto.ForgotPasswordRequest
import com.github.nenadjakic.useraccess.dto.MailRequest
import com.github.nenadjakic.useraccess.dto.RegisterRequest
import com.github.nenadjakic.useraccess.dto.ResetPasswordRequest
import com.github.nenadjakic.useraccess.dto.SignInRequest
import com.github.nenadjakic.useraccess.dto.TokenResponse
import com.github.nenadjakic.useraccess.entity.PasswordResetToken
import com.github.nenadjakic.useraccess.entity.VerificationToken
import com.github.nenadjakic.useraccess.exception.EntityExistsException
import com.github.nenadjakic.useraccess.exception.GeneralException
import com.github.nenadjakic.useraccess.extension.toUser
import com.github.nenadjakic.useraccess.repository.TenantRepository
import com.github.nenadjakic.useraccess.repository.PasswordResetTokenRepository
import com.github.nenadjakic.useraccess.repository.RoleRepository
import com.github.nenadjakic.useraccess.repository.UserRepository
import com.github.nenadjakic.useraccess.repository.VerificationTokenRepository
import com.github.nenadjakic.useraccess.security.model.LocalUserDetails
import com.github.nenadjakic.useraccess.service.JwtService
import com.github.nenadjakic.useraccess.service.RefreshTokenService
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val userAccessManagementProperties: UserAccessManagementProperties,
    private val passwordEncoder: PasswordEncoder,
    private val tenantRepository: TenantRepository
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun authenticate(signInRequest: SignInRequest): TokenResponse {
        return when (signInRequest.grantType) {
            SignInRequest.GrantType.PASSWORD -> {
                val usernamePassword =
                    UsernamePasswordAuthenticationToken(
                        signInRequest.clientId!!.toString() + "|" + signInRequest.username,
                        signInRequest.passwordOrRefreshToken
                    )
                val authUser: Authentication? = authenticationManager.authenticate(usernamePassword)

                createTokenResponse(
                    authUser?.principal as LocalUserDetails,
                    signInRequest.clientId!!
                )
            }
            SignInRequest.GrantType.REFRESH_TOKEN -> {
                val refreshTokenEntity =
                    refreshTokenService.findByTenantIdAndUsernameAndToken(
                        signInRequest.clientId!!,
                        signInRequest.username,
                        signInRequest.passwordOrRefreshToken
                    )
                createTokenResponse(
                    LocalUserDetails(refreshTokenEntity.user),
                    signInRequest.clientId!!
                )
            }

            null -> {
             throw IllegalArgumentException("Grant type must not be null")
            }
        }
    }

    @Transactional
    fun register(registerRequest: RegisterRequest): UUID {
        val user = registerRequest.toUser(passwordEncoder, tenantRepository)

        if (userRepository.existsByUsernameAndTenantId(user.email, user.tenant.id!!)) {
            throw EntityExistsException("Username already exists.")
        }
        val savedUser = userRepository.save(user)

        val verificationToken = verificationTokenRepository.save(VerificationToken(savedUser))

        val mailRequest = MailRequest(
            to = listOf(user.email),
            subject = "Complete Registration!",
            body = "To confirm your account, please click here confirm your e-mail: " +
                    userAccessManagementProperties.verificationUrl.replace("{token}", verificationToken.token),
            isHtml = true
        )

        runCatching {
            rabbitTemplate.convertAndSend(userAccessManagementProperties.mailMq.queueName, mailRequest)
        }.onFailure {
            logger.error("Failed to send message to RabbitMQ", it)
        }

        return savedUser.id!!
    }

    fun verifyEmail(token: String): Unit =
        (verificationTokenRepository
            .findByToken(token)
            .orElseThrow { GeneralException("Confirmation url is incorrect.") }
            .takeIf { it.expireAt.isAfter(OffsetDateTime.now()) }
            ?: throw GeneralException("Confirmation url is expired.")
                ).user.let {
                it.enabled = true
                it.emailConfirmed = true
                userRepository.save(it)
            }

    @Transactional
    fun forgotPassword(request: ForgotPasswordRequest) {
        val user = userRepository
            .findByUsernameAndTenantId(request.username, request.clientId!!)
            .orElseThrow { EntityNotFoundException("User not found") }

        val passwordResetToken = passwordResetTokenRepository.save(PasswordResetToken(user))
        val passwordResetLink = userAccessManagementProperties.passwordResetUrl.replace("{tokent}", passwordResetToken.token)

        val mailRequest = MailRequest(
            to = listOf(user.email),
            subject = "Password Reset Request!",
            body = """
                We received a request to reset your password. If this was you, click the link below to reset your password:

                Reset Password: $passwordResetLink
                
                If you did not request a password reset, please ignore this email.
                
                This link will expire in 1 hour for security reasons.
                """.trimIndent(),
            isHtml = true
        )

        runCatching {
            rabbitTemplate.convertAndSend(userAccessManagementProperties.mailMq.queueName, mailRequest)
        }.onFailure {
            logger.error("Failed to send message to RabbitMQ", it)
        }
    }

    fun changePassword(
        changePasswordRequest: ChangePasswordRequest
    ) {
        val user = userRepository.findByUsernameAndTenantId(changePasswordRequest.username, changePasswordRequest.clientId!!)
            .orElseThrow { EntityNotFoundException("User not found") }

        if (!passwordEncoder.matches(changePasswordRequest.currentPassword, user.password)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        user.password = passwordEncoder.encode(changePasswordRequest.password)
        userRepository.save(user)
    }

    fun resetPassword(request: ResetPasswordRequest) {
        val passwordResetToken = passwordResetTokenRepository.findByToken(request.token)
            .orElseThrow { RuntimeException("Invalid or expired token") }

        if (passwordResetToken.expireAt.isBefore(OffsetDateTime.now())) {
            throw GeneralException("Password reset url was expired.")
        }

        val user = passwordResetToken.user
        user.password = passwordEncoder.encode(request.password)
        userRepository.save(user)
    }

    private fun createTokenResponse(user: LocalUserDetails, tenantId: UUID): TokenResponse {
        val accessToken = jwtService.createToken(user, tenantId.toString())
        val refreshToken = refreshTokenService.create(tenantId, user.username)!!.token
        return TokenResponse(accessToken, refreshToken)
    }
}