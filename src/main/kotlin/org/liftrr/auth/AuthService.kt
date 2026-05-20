package org.liftrr.auth

import org.liftrr.auth.google.OAuthTokenVerifier
import org.liftrr.auth.jwt.JwtService
import org.liftrr.auth.refresh.RefreshTokenService
import org.liftrr.common.EmailAlreadyInUseException
import org.liftrr.common.UserNotPersistedException
import org.liftrr.user.User
import org.liftrr.user.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val oAuthTokenVerifier: OAuthTokenVerifier,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: EmailPasswordRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyInUseException(request.email)
        }
        val user = userRepository.save(
            User(email = request.email, passwordHash = passwordEncoder.encode(request.password))
        )
        return issueTokenPair(user)
    }

    @Transactional
    fun login(request: EmailPasswordRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val user = userRepository.findByEmail(request.email)!!
        return issueTokenPair(user)
    }

    @Transactional
    fun googleAuth(request: GoogleAuthRequest): AuthResponse {
        val payload = oAuthTokenVerifier.verify(request.idToken)

        val user = userRepository.findByEmail(payload.email)
            ?: userRepository.save(
                User(email = payload.email, googleId = payload.subject, name = payload.name)
            )

        if (user.googleId == null) {
            user.googleId = payload.subject
            userRepository.save(user)
        }
        return issueTokenPair(user)
    }

    @Transactional
    fun refresh(request: RefreshRequest): AuthResponse {
        val newRefreshToken = refreshTokenService.rotate(request.refreshToken)
        return issueTokenPair(newRefreshToken.user)
    }

    fun logout(request: RefreshRequest) {
        refreshTokenService.revoke(request.refreshToken)
    }

    private fun issueTokenPair(user: User): AuthResponse =
        user.id?.let { userId ->
            AuthResponse(
                accessToken = jwtService.generateToken(user.email, userId),
                refreshToken = refreshTokenService.create(user).token
            )
        } ?: throw UserNotPersistedException()
}
