package com.github.nenadjakic.useraccess.repository

import com.github.nenadjakic.useraccess.entity.Client
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClientRepository: JpaRepository<Client, UUID>