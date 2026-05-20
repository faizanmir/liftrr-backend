package org.liftrr.workout.dto

data class CreateWorkoutSessionRequest(
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
    val keyFramesJson: String? = null
)
