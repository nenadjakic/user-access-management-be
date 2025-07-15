package com.github.nenadjakic.useraccess.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    schema = "security",
    name = "permissions",
    uniqueConstraints = [
        UniqueConstraint(name = "security_permissions_name_tenant_id", columnNames = [ "name", "tenant_id" ])
    ]
)
class Permission : AbstractNameEntity<UUID>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, length = 36)
    override var id: UUID? = null

    @Column(name = "name", nullable = false, unique = true, length = 75)
    override lateinit var name: String

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    lateinit var tenant: Tenant
}