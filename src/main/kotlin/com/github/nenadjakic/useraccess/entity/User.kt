package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    schema = "security",
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_security_users_username", columnNames = [ "username" ]),
        UniqueConstraint(name = "uq_security_users_email", columnNames = [ "email" ])
    ]
)
class User : AbstractEntity<UUID>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    var provider: Provider = Provider.LOCAL

    @Column(name = "username", unique = true, nullable = false, length = 500)
    lateinit var username: String

    @Column(name = "email", unique = true, nullable = false, length = 500)
    lateinit var email: String

    @Column(name = "password", length = 255)
    lateinit var password: String

    @Column(name = "email_confirmed", nullable = false)
    var emailConfirmed: Boolean = false

    @Column(name = "locked", nullable = false)
    var locked: Boolean = false

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = false

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinTable(
        schema = "security", name = "user_role",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    private val _roles = mutableSetOf<Role>()
    var roles: Set<Role>
        get() = _roles
        set(value) {
            _roles.clear()
            _roles.addAll(value)
        }
   fun addRole(role: Role?) {
        role?.let {
            _roles.add(it)
        }
    }
    fun removeRoleById(role_id: UUID) = _roles.removeIf { it.id == role_id }
}