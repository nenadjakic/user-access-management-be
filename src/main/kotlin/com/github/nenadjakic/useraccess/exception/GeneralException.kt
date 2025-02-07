package com.github.nenadjakic.useraccess.exception

class GeneralException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}