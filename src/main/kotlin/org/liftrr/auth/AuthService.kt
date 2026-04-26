package org.liftrr.auth

import org.liftrr.auth.google.GoogleTokenService
import org.liftrr.auth.jwt.JwtService
import org.liftrr.auth.refresh.RefreshTokenService
import org.liftrr.user.User
import org.liftrr.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val googleTokenService: GoogleTokenService,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: EmailPasswordRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already in use")
        }
        val user = userRepository.save(
            User(email = request.email, passwordHash = passwordEncoder.encode(request.password))
        )
        return issueTokenPair(user)
    }

    fun login(request: EmailPasswordRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val user = userRepository.findByEmail(request.email)!!
        return issueTokenPair(user)
    }

    fun googleAuth(request: GoogleAuthRequest): AuthResponse {
        val payload = googleTokenService.verify(request.idToken)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token")

        val user = userRepository.findByEmail(payload.email)
            ?: userRepository.save(
                User(email = payload.email, googleId = payload.subject, name = payload["name"] as? String)
            )

        if (user.googleId == null) {
            user.googleId = payload.subject
            userRepository.save(user)
        }
        return issueTokenPair(user)
    }

    fun refresh(request: RefreshRequest): AuthResponse {
        val newRefreshToken = refreshTokenService.rotate(request.refreshToken)
        return issueTokenPair(newRefreshToken.user)
    }

    fun logout(request: RefreshRequest) {
        refreshTokenService.revoke(request.refreshToken)
    }

    private fun issueTokenPair(user: User): AuthResponse = AuthResponse(
        accessToken = jwtService.generateToken(user.email, user.id),
        refreshToken = refreshTokenService.create(user).token
    )
}
