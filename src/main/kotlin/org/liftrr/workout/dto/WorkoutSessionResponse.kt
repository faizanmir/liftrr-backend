package org.liftrr.workout.dto

import org.liftrr.workout.WorkoutSession
import java.util.UUID

data class WorkoutSessionResponse(
    val id: UUID,
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
    val repDataJson: String?,
    val keyFramesJson: String?,
    val videoCloudUrl: String?,
    val createdAt: Long
) {
    companion object {
        fun from(session: WorkoutSession) = WorkoutSessionResponse(
            id = session.id!!,
            exerciseType = session.exerciseType,
            totalReps = session.totalReps,
            goodReps = session.goodReps,
            badReps = session.badReps,
            averageQuality = session.averageQuality,
            durationMs = session.durationMs,
            overallScore = session.overallScore,
            grade = session.grade,
            weight = session.weight,
            timestamp = session.timestamp,
            repDataJson = session.repDataJson,
            keyFramesJson = session.keyFramesJson,
            videoCloudUrl = session.videoCloudUrl,
            createdAt = session.createdAt
        )
    }
}
