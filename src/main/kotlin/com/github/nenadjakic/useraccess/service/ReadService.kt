package com.github.nenadjakic.useraccess.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReadService<T, ID> {

    fun findById(id: ID): T?

    fun find(pageable: Pageable): Page<T>
}