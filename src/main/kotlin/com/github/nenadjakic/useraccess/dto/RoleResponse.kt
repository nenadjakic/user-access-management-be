package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.entity.Role
import java.util.UUID

data class RoleResponse(
    val id: UUID,
    val name: String
) {
    constructor(role: Role) : this(
        id = role.id ?: UUID.randomUUID(),
        name = role.name,
    )
}