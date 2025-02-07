package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.Column

abstract class AbstractNameEntity<T> : AbstractEntity<T>() {
    @Column
    lateinit var name: String
}