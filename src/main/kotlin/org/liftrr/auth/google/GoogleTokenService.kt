package org.liftrr.auth.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.liftrr.common.InvalidGoogleTokenException
import org.springframework.stereotype.Service

@Service
class GoogleTokenService(private val verifier: GoogleIdTokenVerifier) : OAuthTokenVerifier {

    override fun verify(idToken: String): OAuthPayload {
        val payload = verifier.verify(idToken)?.payload
            ?: throw InvalidGoogleTokenException()
        return OAuthPayload(
            email = payload.email,
            subject = payload.subject,
            name = payload["name"] as? String
        )
    }
}
