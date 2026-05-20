package org.liftrr.workout

import jakarta.persistence.*
import org.liftrr.user.User
import java.util.UUID

@Entity
@Table(
    name = "workout_sessions",
    indexes = [Index(name = "idx_workout_sessions_user_deleted", columnList = "user_id, is_deleted")]
)
class WorkoutSession(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val exerciseType: String,

    val totalReps: Int,
    val goodReps: Int,
    val badReps: Int,
    val averageQuality: Float,
    val durationMs: Long,
    val overallScore: Float,
    val grade: String,
    val weight: Float?,
    val timestamp: Long,

    @Column(columnDefinition = "TEXT")
    val repDataJson: String? = null,

    @Column(columnDefinition = "TEXT")
    val keyFramesJson: String? = null,

    @Column(columnDefinition = "TEXT")
    val keyFramesCloudUrls: String? = null,

    var videoCloudUrl: String? = null,

    val createdAt: Long = System.currentTimeMillis(),

    var isDeleted: Boolean = false,
    var deletedAt: Long? = null
)
