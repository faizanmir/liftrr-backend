package org.liftrr.userprofile.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User profile creation/update payload")
data class UserProfileRequest(
    @param:Schema(description = "First name", example = "John") val firstName: String?,
    @param:Schema(description = "Last name", example = "Doe") val lastName: String?,
    @param:Schema(description = "Profile photo URL") val photoUrl: String?
)
