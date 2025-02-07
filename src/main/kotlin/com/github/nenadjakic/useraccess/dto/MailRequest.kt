package com.github.nenadjakic.useraccess.dto

data class MailRequest(
    val to: Collection<String>,
    val cc: Collection<String>? = null,
    val bcc: Collection<String>? = null,
    val subject: String,
    val body: String,
    val isHtml: Boolean = false
)