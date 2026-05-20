package org.liftrr.userprofile

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.liftrr.userprofile.dto.UserProfileRequest
import org.liftrr.userprofile.dto.UserProfileResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "User Profile", description = "Create, fetch, update and delete the authenticated user's profile")
@SecurityRequirement(name = "Bearer")
class UserProfileController(private val userProfileService: UserProfileService) {

    @Operation(summary = "Create profile", description = "Creates a profile for the authenticated user.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Profile created"),
        ApiResponse(
            responseCode = "404",
            description = "Authenticated user not found",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "409",
            description = "Profile already exists",
            content = [Content(schema = Schema(hidden = true))]
        )
    )
    @PostMapping
    fun createProfile(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestBody request: UserProfileRequest
    ): ResponseEntity<UserProfileResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(userProfileService.createProfile(principal.username, request))

    @Operation(summary = "Get profile", description = "Returns the authenticated user's profile.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Profile returned"),
        ApiResponse(
            responseCode = "404",
            description = "Profile not found",
            content = [Content(schema = Schema(hidden = true))]
        )
    )
    @GetMapping
    fun fetchProfile(
        @AuthenticationPrincipal principal: UserDetails
    ): UserProfileResponse = userProfileService.fetchProfile(principal.username)

    @Operation(
        summary = "Update profile",
        description = "Partially updates the authenticated user's profile. Omitted fields retain their current values."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Profile updated"),
        ApiResponse(
            responseCode = "404",
            description = "Profile not found",
            content = [Content(schema = Schema(hidden = true))]
        )
    )
    @PatchMapping
    fun editProfile(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestBody request: UserProfileRequest
    ): UserProfileResponse = userProfileService.editProfile(principal.username, request)

    @Operation(summary = "Delete profile", description = "Deletes the authenticated user's profile.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Profile deleted"),
        ApiResponse(
            responseCode = "404",
            description = "Profile not found",
            content = [Content(schema = Schema(hidden = true))]
        )
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProfile(
        @AuthenticationPrincipal principal: UserDetails
    ) = userProfileService.deleteProfile(principal.username)


}
