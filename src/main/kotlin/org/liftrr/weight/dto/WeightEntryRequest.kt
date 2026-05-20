package org.liftrr.weight.dto

data class WeightEntryRequest(
    val exerciseType: String,
    val weight: Float,
    val timestamp: Long
)

data class BulkWeightUpsertRequest(
    val entries: List<WeightEntryRequest>
)
