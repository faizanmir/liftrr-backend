package org.liftrr.workout

import jakarta.annotation.Generated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

data class WorkoutSessionEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    // Core workout data
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

    val repDataJson: String? = null,
    val keyFramesJson: String? = null,       // Serialized list of KeyFrame objects

    // Cloud storage URLs
    val videoCloudUrl: String? = null,       // S3/Cloud Storage URL
    val keyFramesCloudUrls: String? = null,  // JSON array of cloud URLs

    // User association
    val userId: String,                       // Owner of this workout

    // Sync tracking
    val lastSyncedAt: Long? = null,          // Timestamp of last successful sync
    val version: Int = 1,                    // For optimistic locking/conflict resolution
)