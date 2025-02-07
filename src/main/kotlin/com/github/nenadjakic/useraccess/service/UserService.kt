package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.config.UserAccessManagementProperties
import com.github.nenadjakic.useraccess.dto.ForgotPasswordRequest
import com.github.nenadjakic.useraccess.dto.MailRequest
import com.github.nenadjakic.useraccess.dto.ResetPasswordRequest
import com.github.nenadjakic.useraccess.entity.PasswordResetToken
import com.github.nenadjakic.useraccess.entity.User
import com.github.nenadjakic.useraccess.entity.VerificationToken
import com.github.nenadjakic.useraccess.exception.EntityExistsException
import com.github.nenadjakic.useraccess.exception.GeneralException
import com.github.nenadjakic.useraccess.repository.PasswordResetTokenRepository
import com.github.nenadjakic.useraccess.repository.UserRepository
import com.github.nenadjakic.useraccess.repository.VerificationTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val userAccessManagementProperties: UserAccessManagementProperties,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun findByUsername(username: String): User? = findByEmail(username)

    fun create(user: User): User {
        if (userRepository.existsByEmail(user.email)) {
            throw EntityExistsException("Username already exists.")
        }
        val savedUser = userRepository.save(user)

        val verificationToken = verificationTokenRepository.save(VerificationToken(savedUser))

        val mailRequest = MailRequest(
            to = listOf(user.email),
            subject = "Complete Registration!",
            body = "To confirm your account, please click here confirm your e-mail: " +
                    "http://localhost:8080/auth/confirm-email?token=" + verificationToken.id,
            isHtml = true
        )

        runCatching {
            rabbitTemplate.convertAndSend(userAccessManagementProperties.mailMq.queueName, mailRequest)
        }.onFailure {
            logger.error("Failed to send message to RabbitMQ", it)
        }

        return savedUser
    }

    open fun verifyEmail(token: String) {
        val verificationToken = verificationTokenRepository.findByToken(token) ?: throw GeneralException("Confirmation url is incorrect.")

        if (verificationToken.expireAt.isBefore(OffsetDateTime.now())) {
            throw GeneralException("Verification url was expired.")
        }

        verificationToken.user.apply {
            enabled = true
            emailConfirmed = true
            userRepository.save(this)
        }
    }

    open fun changePassword(
        username: String,
        currentPassword: String,
        newPassword: String
    ) {
        val user = userRepository.findByUsername(username)
            ?: throw GeneralException("User not found")

        if (!passwordEncoder.matches(currentPassword, user.password)) {
            throw GeneralException("Current password is incorrect")
        }

        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }

    open fun forgotPassword(request: ForgotPasswordRequest) {
        val user = userRepository.findByUsername(request.username) ?: throw EntityExistsException("User not found")

        val passwordResetToken = PasswordResetToken(user)
        passwordResetTokenRepository.save(passwordResetToken)
        val passwordResetLink = "http://localhost:8080/auth/reset-password?token=" + passwordResetToken.id

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

    open fun resetPassword(request: ResetPasswordRequest) {
        val passwordResetToken = passwordResetTokenRepository.findById(UUID.fromString(request.token))
            .orElseThrow { RuntimeException("Invalid or expired token") }

        if (passwordResetToken.expireAt.isBefore(OffsetDateTime.now())) {
            throw GeneralException("Password reset url was expired.")
        }

        val user = passwordResetToken.user
        user.password = passwordEncoder.encode(request.password)
        userRepository.save(user)
    }
}