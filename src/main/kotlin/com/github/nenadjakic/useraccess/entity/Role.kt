package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    schema = "security",
    name = "roles",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_security_roles_name", columnNames = ["name"])
    ]
)
@AttributeOverride(name = "name", column = Column(name = "name", nullable = false, unique = true, length = 255))
class Role : AbstractNameEntity<UUID>() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(schema = "security", name = "role_permission",
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id", referencedColumnName = "id")])
    private val _permissions = mutableSetOf<Permission>()
    var permissions: Set<Permission>
        get() = _permissions
        set(value) {
            _permissions.clear()
            _permissions.addAll(value)
        }

    @ManyToMany(mappedBy = "_roles", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    private val _users = mutableSetOf<User>()
    var users: Set<User>
        get() = _users
        set(value) {
            _users.clear()
            _users.addAll(value)
        }
}