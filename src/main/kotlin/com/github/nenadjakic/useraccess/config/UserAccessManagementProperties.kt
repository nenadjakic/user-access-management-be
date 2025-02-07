package com.github.nenadjakic.useraccess.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "user-access-management")
class UserAccessManagementProperties {

    var mailMq: MailMqProperties = MailMqProperties()

    class MailMqProperties {
        lateinit var queueName: String
    }
}