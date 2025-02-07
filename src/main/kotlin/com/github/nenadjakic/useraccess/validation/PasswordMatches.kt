package com.github.nenadjakic.useraccess.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordMatchesValidator::class])
annotation class PasswordMatches(
    val message: String = "Passwords do not match",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
