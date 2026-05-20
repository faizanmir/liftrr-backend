package org.liftrr.auth

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Email and password payload")
data class EmailPasswordRequest(
    @param:Schema(description = "User's email address", example = "user@example.com") val email: String,
    @param:Schema(description = "Password (min 8 chars recommended)", example = "s3cr3tP@ss") val password: String
)

@Schema(description = "Google Sign-In payload")
data class GoogleAuthRequest(
    @param:Schema(description = "Google ID token obtained from the Google Sign-In SDK on the client") val idToken: String
)

@Schema(description = "Refresh token payload")
data class RefreshRequest(
    @param:Schema(description = "The refresh token issued at login/register") val refreshToken: String
)

@Schema(description = "Successful authentication response")
data class AuthResponse(
    @param:Schema(description = "Short-lived JWT access token (15 min). Send as 'Authorization: Bearer <token>'") val accessToken: String,
    @param:Schema(description = "Long-lived refresh token (7 days). Store securely and use to obtain new access tokens.") val refreshToken: String
)

@Schema(description = "Authenticated user identity")
data class CurrentUserResponse(
    @param:Schema(description = "Backend user ID") val userId: String,
    @param:Schema(description = "User email") val email: String,
    @param:Schema(description = "Display/name from account provider") val name: String?,
    @param:Schema(description = "Whether this account is linked to Google") val hasGoogleAuth: Boolean,
    @param:Schema(description = "Whether this account has email/password auth") val hasPasswordAuth: Boolean
)
