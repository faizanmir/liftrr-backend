package org.liftrr.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Register, log in, and manage tokens")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "Register with email & password", description = "Creates a new account and returns an access + refresh token pair.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Account created"),
        ApiResponse(responseCode = "409", description = "Email already in use", content = [Content(schema = Schema(hidden = true))])
    )
    @PostMapping("/register")
    fun register(@RequestBody request: EmailPasswordRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request))

    @Operation(summary = "Log in with email & password", description = "Authenticates an existing user and returns a fresh token pair.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Authenticated"),
        ApiResponse(responseCode = "401", description = "Invalid credentials", content = [Content(schema = Schema(hidden = true))])
    )
    @PostMapping("/login")
    fun login(@RequestBody request: EmailPasswordRequest): AuthResponse =
        authService.login(request)

    @Operation(summary = "Log in with Google", description = "Exchanges a Google ID token for a Liftrr token pair. Creates the account automatically on first sign-in.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Authenticated"),
        ApiResponse(responseCode = "401", description = "Invalid or expired Google token", content = [Content(schema = Schema(hidden = true))])
    )
    @PostMapping("/google")
    fun googleAuth(@RequestBody request: GoogleAuthRequest): AuthResponse =
        authService.googleAuth(request)

    @Operation(summary = "Refresh tokens", description = "Exchanges a valid refresh token for a new access + refresh token pair. The old token is immediately revoked.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "New token pair issued"),
        ApiResponse(responseCode = "401", description = "Refresh token invalid, expired, or already used", content = [Content(schema = Schema(hidden = true))])
    )
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): AuthResponse =
        authService.refresh(request)

    @Operation(summary = "Log out", description = "Revokes the supplied refresh token, invalidating this device's session.")
    @ApiResponses(ApiResponse(responseCode = "204", description = "Logged out"))
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@RequestBody request: RefreshRequest) =
        authService.logout(request)
}
