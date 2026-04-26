package org.liftrr.auth.refresh

import org.liftrr.user.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${jwt.refresh-expiration-ms}") private val refreshExpirationMs: Long
) {

    fun create(user: User): RefreshToken = refreshTokenRepository.save(
        RefreshToken(
            token = UUID.randomUUID().toString(),
            user = user,
            expiresAt = Instant.now().plusMillis(refreshExpirationMs)
        )
    )

    /**
     * Validates the token, then rotates it: revokes the old one and issues a fresh one.
     * Returns the new RefreshToken.
     */
    @Transactional
    fun rotate(rawToken: String): RefreshToken {
        val existing = refreshTokenRepository.findByToken(rawToken)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found")

        if (existing.revoked) {
            // Token was already used — possible replay attack; revoke all tokens for this user
            refreshTokenRepository.revokeAllForUser(existing.user.id)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token already used")
        }

        if (existing.expiresAt.isBefore(Instant.now())) {
            existing.revoked = true
            refreshTokenRepository.save(existing)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired")
        }

        existing.revoked = true
        refreshTokenRepository.save(existing)
        return create(existing.user)
    }

    @Transactional
    fun revoke(rawToken: String) {
        val token = refreshTokenRepository.findByToken(rawToken) ?: return
        token.revoked = true
        refreshTokenRepository.save(token)
    }
}
