package org.liftrr.auth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret}") secret: String,
    @param:Value("\${jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

    fun generateToken(email: String, userId: UUID): String = Jwts.builder()
        .subject(email)
        .claim("uid", userId.toString())
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + expirationMs))
        .signWith(key)
        .compact()

    fun extractEmail(token: String): String = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .payload
        .subject
}
