package org.liftrr.weight.dto

import org.liftrr.weight.WeightEntry

data class WeightEntryResponse(
    val id: String,
    val exerciseType: String,
    val weight: Float,
    val timestamp: Long,
    val createdAt: Long
) {
    companion object {
        fun from(entry: WeightEntry) = WeightEntryResponse(
            id = entry.id!!.toString(),
            exerciseType = entry.exerciseType,
            weight = entry.weight,
            timestamp = entry.timestamp,
            createdAt = entry.createdAt
        )
    }
}
