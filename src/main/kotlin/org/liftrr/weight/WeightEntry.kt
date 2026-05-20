package org.liftrr.weight

import jakarta.persistence.*
import org.liftrr.user.User
import java.util.UUID

@Entity
@Table(
    name = "weight_entries",
    indexes = [Index(name = "idx_weight_entries_user", columnList = "user_id, exercise_type")]
)
class WeightEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "exercise_type", nullable = false)
    val exerciseType: String,

    @Column(nullable = false)
    var weight: Float,

    @Column(nullable = false)
    var timestamp: Long = System.currentTimeMillis(),

    val createdAt: Long = System.currentTimeMillis()
)
