package com.github.nenadjakic.useraccess.dto

import com.github.nenadjakic.useraccess.entity.Role
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class RoleRequest(
    @NotEmpty
    val name: String
) {

    /**
     * Converts this RoleRequest into a Role entity.
     *
     * @return a Role entity with properties mapped from this RoleRequest
     */
    fun toRole(): Role = Role().apply {
        this.name = this@RoleRequest.name
    }
}