package org.liftrr.user

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column
    var passwordHash: String? = null,

    @Column
    var googleId: String? = null,

    @Column
    var name: String? = null
)
