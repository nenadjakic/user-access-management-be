package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.SQLRestriction
import java.util.UUID

@Entity
@Table( schema = "security", name = "clients", uniqueConstraints = [
    UniqueConstraint(name = "uq_security_clients_name", columnNames = [ "name" ])
])
@SQLRestriction("is_active = true")
class Client : AbstractNameEntity<UUID>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @Column(name = "name", nullable = false, unique = true, length = 75)
    override lateinit var name: String

    @Column(name = "private_key_path", nullable = false, length = 255)
    lateinit var privateKeyPath: String

    @Column(name = "public_key_path", nullable = false, length = 255)
    lateinit var publicKeyPath: String

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
}