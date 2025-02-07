package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID>