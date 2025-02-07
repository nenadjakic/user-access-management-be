package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    schema = "security",
    name = "refresh_token",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_security_refresh_token_token", columnNames = [ "token" ])
    ])
class RefreshToken() : AbstractEntity<UUID>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @Column(name = "token", nullable = false, length = 100, unique = true)
    lateinit var token: String

    @Column(name = "expire_at", nullable = false)
    lateinit var expireAt: OffsetDateTime

    constructor(user: User) : this() {
        this.user = user
        this.token = UUID.randomUUID().toString().replace("-", "")
        this.expireAt = OffsetDateTime.now().plusHours(12L)
    }
}