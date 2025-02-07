package com.github.nenadjakic.useraccess.entity

abstract class AbstractEntity<T> : Auditable<T>() {
    abstract var id: T?
}