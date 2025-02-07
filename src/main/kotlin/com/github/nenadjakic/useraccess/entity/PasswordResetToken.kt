package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    schema = "security",
    name = "password_reset_token"
)
class PasswordResetToken() : AbstractEntity<UUID>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    lateinit var user: User

    @Column(name = "expire_at", nullable = false)
    lateinit var expireAt: OffsetDateTime

    constructor(user: User) : this() {
        this.user = user
        this.expireAt = OffsetDateTime.now().plusHours(12L)
    }
}