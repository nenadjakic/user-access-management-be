package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.Column

abstract class AbstractNameEntity<ID> : AbstractEntity<ID>() {
    abstract var name: String
}