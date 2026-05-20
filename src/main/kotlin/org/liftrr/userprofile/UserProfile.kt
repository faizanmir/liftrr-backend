package org.liftrr.userprofile

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.liftrr.user.User
import java.time.Instant
import java.util.*

@Entity
@Table(name = "user_profiles")
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id", nullable = false, unique = true,
        foreignKey = ForeignKey(name = "fk_user_profiles_user_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    val user: User,

    @Column
    var firstName: String? = null,

    @Column
    var lastName: String? = null,

    @Column
    var photoUrl: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var modifiedAt: Instant = Instant.now(),

    @Column
    var lastSyncedAt: Instant? = null,

    @Version
    var version: Int = 0
)
