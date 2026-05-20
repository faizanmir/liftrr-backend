package org.liftrr.workout

import org.liftrr.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkoutSessionRepository : JpaRepository<WorkoutSession, UUID> {
    fun findAllByUserAndIsDeletedFalse(user: User): List<WorkoutSession>
}
