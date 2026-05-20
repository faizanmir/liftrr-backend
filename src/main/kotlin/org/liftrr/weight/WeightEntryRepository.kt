package org.liftrr.weight

import org.liftrr.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WeightEntryRepository : JpaRepository<WeightEntry, UUID> {
    fun findAllByUserOrderByTimestampDesc(user: User): List<WeightEntry>
    fun findByUserAndExerciseType(user: User, exerciseType: String): WeightEntry?
}
