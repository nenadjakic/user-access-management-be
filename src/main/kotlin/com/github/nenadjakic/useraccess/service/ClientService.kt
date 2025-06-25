package com.github.nenadjakic.useraccess.service

import com.github.nenadjakic.useraccess.entity.Client
import com.github.nenadjakic.useraccess.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class ClientService(
    private val clientRepository: ClientRepository
) {
    fun findAll(): List<Client> =
        clientRepository.findAll()
}