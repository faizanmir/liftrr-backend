package org.liftrr.userprofile.photo

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/profile/photo")
@Tag(name = "Profile Photo", description = "Manage profile photo upload")
@SecurityRequirement(name = "Bearer")
class UserPhotoController(private val userPhotoService: UserPhotoService) {

    @Operation(
        summary = "Request photo upload URL",
        description = "Returns a presigned URL to upload a photo directly to storage. Use the returned objectKey to confirm the upload."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Presigned URL returned"),
        ApiResponse(
            responseCode = "404",
            description = "Profile not found",
            content = [Content(schema = Schema(hidden = true))]
        )
    )
    @PostMapping
    fun requestUploadUrl(
        @AuthenticationPrincipal principal: UserDetails
    ): PhotoUploadUrlResponse = userPhotoService.requestUploadUrl(principal.username)

    @Operation(
        summary = "Confirm photo upload",
        description = "Verifies the photo exists in storage and sets it as the profile photo. Deletes the previous photo if one existed."
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Photo confirmed and set"),
        ApiResponse(
            responseCode = "403",
            description = "Object key does not belong to this user",
            content = [Content(schema = Schema(hidden = true))]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Photo not found in storage",
            content = [Content(schema = Schema(hidden = true))]
        )
    )
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun confirmUpload(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestParam objectKey: String
    ) = userPhotoService.confirmUpload(principal.username, objectKey)
}
