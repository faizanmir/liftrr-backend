package org.liftrr.workout

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.liftrr.workout.dto.CreateWorkoutSessionRequest
import org.liftrr.workout.dto.WorkoutSessionResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/workout/session")
@Tag(name = "Workout Session", description = "Manage workout sessions")
@SecurityRequirement(name = "Bearer")
class WorkoutMetaDataController(private val workoutSessionService: WorkoutSessionService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestBody request: CreateWorkoutSessionRequest
    ): WorkoutSessionResponse = workoutSessionService.create(principal.username, request)

    @GetMapping
    fun list(
        @AuthenticationPrincipal principal: UserDetails
    ): List<WorkoutSessionResponse> = workoutSessionService.listForUser(principal.username)

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal principal: UserDetails,
        @PathVariable sessionId: UUID
    ) = workoutSessionService.softDelete(principal.username, sessionId)
}
