package org.liftrr.workout.video

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/workout/video")
@Tag(name = "Workout Video", description = "Manage workout video upload")
@SecurityRequirement(name = "Bearer")
class WorkoutVideoController(private val workoutVideoUrlProviderService: WorkoutVideoUrlProviderService) {

    @PostMapping
    fun requestUploadUrl(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestParam sessionId: UUID
    ): VideoUploadResponse = workoutVideoUrlProviderService.requestUploadUrl(principal.username, sessionId)

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun confirmUpload(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestParam objectKey: String
    ) = workoutVideoUrlProviderService.confirmUpload(principal.username, objectKey)
}
