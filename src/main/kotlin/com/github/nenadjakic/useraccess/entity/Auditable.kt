package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class Auditable<U>: Serializable {

    @CreatedDate
    @Column(name = "created_date_time", updatable = false)
    lateinit var createdDateTime: LocalDateTime

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    var createdBy: U? = null

    @LastModifiedDate
    @Column(name = "last_modified_date_time")
    lateinit var lastModifiedDate: LocalDateTime

    @LastModifiedBy
    @Column(name = "last_modified_by")
    var lastModifiedBy: U? = null
}