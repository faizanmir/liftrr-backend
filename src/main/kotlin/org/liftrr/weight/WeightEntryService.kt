package org.liftrr.weight

import org.liftrr.user.UserService
import org.liftrr.weight.dto.BulkWeightUpsertRequest
import org.liftrr.weight.dto.WeightEntryResponse
import org.springframework.stereotype.Service

@Service
class WeightEntryService(
    private val userService: UserService,
    private val weightEntryRepository: WeightEntryRepository
) {

    fun listForUser(email: String): List<WeightEntryResponse> {
        val (user, _) = userService.resolveUser(email)
        return weightEntryRepository.findAllByUserOrderByTimestampDesc(user)
            .map { WeightEntryResponse.from(it) }
    }

    fun upsertBulk(email: String, request: BulkWeightUpsertRequest): List<WeightEntryResponse> {
        val (user, _) = userService.resolveUser(email)
        val results = request.entries.map { entry ->
            val existing = weightEntryRepository.findByUserAndExerciseType(user, entry.exerciseType)
            if (existing != null) {
                if (entry.timestamp > existing.timestamp) {
                    existing.weight = entry.weight
                    existing.timestamp = entry.timestamp
                    weightEntryRepository.save(existing)
                } else {
                    existing
                }
            } else {
                weightEntryRepository.save(
                    WeightEntry(
                        user = user,
                        exerciseType = entry.exerciseType,
                        weight = entry.weight,
                        timestamp = entry.timestamp
                    )
                )
            }
        }
        return results.map { WeightEntryResponse.from(it) }
    }
}
