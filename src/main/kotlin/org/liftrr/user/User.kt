package org.liftrr.user

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val email: String,

    @Column
    var passwordHash: String? = null,

    @Column
    var googleId: String? = null,

    @Column
    var name: String? = null
)
