package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.entity.User
import java.util.UUID

/**
 * A data transfer object representing a user's response details.
 *
 * @property username the username of the user
 * @property email the email address of the user
 * @property emailConfirmed indicates if the user's email is confirmed
 * @property locked indicates if the user's account is locked
 * @property enabled indicates if the user's account is enabled
 * @property roles a collection of role names assigned to the user
 */
data class UserResponse private constructor(
    val id: UUID,
    val username: String,
    val email: String,
    val emailConfirmed: Boolean,
    val locked: Boolean,
    val enabled: Boolean,
    val roles: Collection<String>
) {
    companion object {
        /**
         * Constructs a `UserResponse` from a `User` entity.
         *
         * @param user the user entity from which to construct the response
         */
        fun from(user: User): UserResponse =
            UserResponse(
                id = user.id!!,
                username = user.username,
                email = user.email,
                emailConfirmed = user.emailConfirmed,
                locked = user.locked,
                enabled = user.enabled,
                roles = user.roles.map { it.name }
            )
    }
}