package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.Permission
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PermissionRepository : JpaRepository<Permission, UUID>