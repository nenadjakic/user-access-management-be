package com.github.nenadjakic.useraccess.security.model

import com.github.nenadjakic.useraccess.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

class LocalUserDetails() : UserDetails {
    private var id: UUID? = null
    private val authorities= mutableSetOf<GrantedAuthority>()
    private lateinit var username: String
    private lateinit var password: String
    private var locked = false
    private var enabled = false

    constructor(user: User) : this() {
        id = user.id
        username = user.username
        password = user.password
        enabled = user.enabled
        user.roles.forEach { role ->
            role.permissions.forEach { permission -> authorities.add(SimpleGrantedAuthority("${role.name}_${permission.name}")) }
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = !locked

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled
}