package com.github.nenadjakic.useraccess.entity

abstract class AbstractNameEntity<ID> : AbstractEntity<ID>() {
    abstract var name: String
}