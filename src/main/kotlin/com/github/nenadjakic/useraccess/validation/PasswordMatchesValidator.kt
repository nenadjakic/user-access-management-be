package com.github.nenadjakic.useraccess.validation

import com.github.nenadjakic.useraccess.dto.ConfirmPassword
import com.github.nenadjakic.useraccess.dto.PasswordChangeRequest
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordMatchesValidator : ConstraintValidator<PasswordMatches, ConfirmPassword> {
    override fun isValid(value: ConfirmPassword, context: ConstraintValidatorContext): Boolean {
        val isValid = value.password == value.confirmedPassword

        if (!isValid) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("passwordConfirmation")
                .addConstraintViolation()
        }

        return isValid
    }
}