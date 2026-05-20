package org.liftrr.workout

import org.liftrr.common.InvalidObjectKeyException
import org.liftrr.common.WorkoutSessionNotFoundException
import org.liftrr.user.UserService
import org.liftrr.workout.dto.CreateWorkoutSessionRequest
import org.liftrr.workout.dto.WorkoutSessionResponse
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WorkoutSessionService(
    private val userService: UserService,
    private val workoutSessionRepository: WorkoutSessionRepository
) {

    fun create(email: String, request: CreateWorkoutSessionRequest): WorkoutSessionResponse {
        val (user, _) = userService.resolveUser(email)
        val session = WorkoutSession(
            user = user,
            exerciseType = request.exerciseType,
            totalReps = request.totalReps,
            goodReps = request.goodReps,
            badReps = request.badReps,
            averageQuality = request.averageQuality,
            durationMs = request.durationMs,
            overallScore = request.overallScore,
            grade = request.grade,
            weight = request.weight,
            timestamp = request.timestamp,
            repDataJson = request.repDataJson,
            keyFramesJson = request.keyFramesJson
        )
        return WorkoutSessionResponse.from(workoutSessionRepository.save(session))
    }

    fun listForUser(email: String): List<WorkoutSessionResponse> {
        val (user, _) = userService.resolveUser(email)
        return workoutSessionRepository.findAllByUserAndIsDeletedFalse(user)
            .map { WorkoutSessionResponse.from(it) }
    }

    fun softDelete(email: String, sessionId: UUID) {
        val (_, userId) = userService.resolveUser(email)
        val session = workoutSessionRepository.findById(sessionId).orElse(null)
            ?: throw WorkoutSessionNotFoundException(sessionId)
        if (session.user.id != userId) throw InvalidObjectKeyException("session $sessionId")
        session.isDeleted = true
        session.deletedAt = System.currentTimeMillis()
        workoutSessionRepository.save(session)
    }

    fun getSessionForUser(sessionId: UUID, userId: UUID): WorkoutSession {
        val session = workoutSessionRepository.findById(sessionId).orElse(null)
            ?: throw WorkoutSessionNotFoundException(sessionId)
        if (session.user.id != userId) throw WorkoutSessionNotFoundException(sessionId)
        return session
    }

    fun saveVideoUrl(session: WorkoutSession) {
        workoutSessionRepository.save(session)
    }
}
