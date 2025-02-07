package com.github.nenadjakic.useraccess.service

interface WriteService<T, ID> {

    fun create(entity: T): T

    fun update(entity: T): T

    fun delete(entity: T)

    fun deleteById(id: ID)
}