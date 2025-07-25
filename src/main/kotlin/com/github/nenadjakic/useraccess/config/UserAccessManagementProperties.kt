package com.github.nenadjakic.useraccess.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "user-access-management")
class UserAccessManagementProperties {

    lateinit var verificationUrl: String
    lateinit var passwordResetUrl: String
    var mailMq: MailMqProperties = MailMqProperties()
    var jwt: JwtProperties = JwtProperties()
    var security: SecurityProperties = SecurityProperties()

    class MailMqProperties {
        lateinit var queueName: String
    }

    class JwtProperties {
        lateinit var issuer: String
        var validMinutes: Long = 10
    }
    class SecurityProperties {
        var cors: CorsProperties = CorsProperties()
    }

    class CorsProperties {
        lateinit var allowedOrigins: List<String>
    }
}