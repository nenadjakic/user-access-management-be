package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.entity.Role
import java.util.UUID

data class RoleResponse(
    val id: UUID,
    val name: String
) {
    companion object {
        fun from(role: Role): RoleResponse =
            RoleResponse(
                role.id!!,
                role.name
            )
    }

}