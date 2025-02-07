package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    schema = "security",
    name = "permissions",
    uniqueConstraints = [
        UniqueConstraint(name = "security_permissions_name", columnNames = [ "name" ])
    ]
)
class Permission : AbstractNameEntity<UUID>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @ManyToMany(mappedBy = "_permissions")
    private val _roles: MutableList<Role> = mutableListOf()
    var roles: List<Role>
        get() = _roles.toList()
        set(value) {
            _roles.clear()
            _roles.addAll(value)
        }

    fun addRole(role: Role) {
        _roles.add(role)
    }
}