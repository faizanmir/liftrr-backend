package org.liftrr.weight

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.liftrr.weight.dto.BulkWeightUpsertRequest
import org.liftrr.weight.dto.WeightEntryResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/weights")
@Tag(name = "Weights", description = "Per-exercise weight tracking for cross-device restore")
@SecurityRequirement(name = "Bearer")
class WeightEntryController(private val weightEntryService: WeightEntryService) {

    @GetMapping
    fun listWeights(
        @AuthenticationPrincipal principal: UserDetails
    ): List<WeightEntryResponse> = weightEntryService.listForUser(principal.username)

    @PostMapping("/bulk")
    fun bulkUpsert(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestBody request: BulkWeightUpsertRequest
    ): List<WeightEntryResponse> = weightEntryService.upsertBulk(principal.username, request)
}
