package org.liftrr.config.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class GoogleAuthVerificationConfig {

    @Bean
    fun googleVerifier(@Value("\${google.client-id}") clientId: String): GoogleIdTokenVerifier {
        return Builder(newTrustedTransport(), GsonFactory.getDefaultInstance())
            .setAudience(listOf(clientId))
            .build()
    }
}