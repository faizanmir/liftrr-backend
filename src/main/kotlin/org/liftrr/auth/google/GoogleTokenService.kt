package org.liftrr.auth.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GoogleTokenService(private val verifier: GoogleIdTokenVerifier) {

    /** Returns the token payload, or null if the token is invalid. */
    fun verify(idToken: String): GoogleIdToken.Payload? =
        verifier.verify(idToken)?.payload
}