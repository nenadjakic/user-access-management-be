package com.github.nenadjakic.useraccess.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "user-access-management")
class UserAccessManagementProperties {

    var mailMq: MailMqProperties = MailMqProperties()
    var jwt: JwtProperties = JwtProperties()
    var clients: Map<String, ClientConfig> = emptyMap()

    class MailMqProperties {
        lateinit var queueName: String
    }

    class JwtProperties {
        var validMinutes: Long = 10
    }

    class ClientConfig {
        lateinit var clientId: String
        lateinit var privateKeyPath: String
        lateinit var publicKeyPath: String
    }
}