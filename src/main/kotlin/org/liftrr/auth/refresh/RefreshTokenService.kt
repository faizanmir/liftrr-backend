package org.liftrr.auth.refresh

import org.liftrr.common.ExpiredRefreshTokenException
import org.liftrr.common.InvalidRefreshTokenException
import org.liftrr.common.ReplayedRefreshTokenException
import org.liftrr.user.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

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
            ?: throw InvalidRefreshTokenException()

        if (existing.revoked) {
            existing.user.id?.let { refreshTokenRepository.revokeAllForUser(it) }
            throw ReplayedRefreshTokenException()
        }

        if (existing.expiresAt.isBefore(Instant.now())) {
            existing.revoked = true
            refreshTokenRepository.save(existing)
            throw ExpiredRefreshTokenException()
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
