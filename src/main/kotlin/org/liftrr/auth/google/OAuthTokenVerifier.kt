package org.liftrr.auth.google

data class OAuthPayload(
    val email: String,
    val subject: String,
    val name: String?
)

interface OAuthTokenVerifier {
    /** Returns payload or throws [org.liftrr.common.InvalidGoogleTokenException] if token is invalid. */
    fun verify(idToken: String): OAuthPayload
}
