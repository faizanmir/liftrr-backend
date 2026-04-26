package org.liftrr.auth

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Email and password payload")
data class EmailPasswordRequest(
    @param:Schema(description = "User's email address", example = "user@example.com") val email: String,
    @param:Schema(description = "Password (min 8 chars recommended)", example = "s3cr3tP@ss") val password: String
)

@Schema(description = "Google Sign-In payload")
data class GoogleAuthRequest(
    @Schema(description = "Google ID token obtained from the Google Sign-In SDK on the client") val idToken: String
)

@Schema(description = "Refresh token payload")
data class RefreshRequest(
    @Schema(description = "The refresh token issued at login/register") val refreshToken: String
)

@Schema(description = "Successful authentication response")
data class AuthResponse(
    @Schema(description = "Short-lived JWT access token (15 min). Send as 'Authorization: Bearer <token>'") val accessToken: String,
    @Schema(description = "Long-lived refresh token (7 days). Store securely and use to obtain new access tokens.") val refreshToken: String,
)
