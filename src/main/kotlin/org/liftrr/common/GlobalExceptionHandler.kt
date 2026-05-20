package org.liftrr.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

private fun error(status: HttpStatus, message: String, path: String) =
    ResponseEntity.status(status).body(
        ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = path
        )
    )

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: Instant = Instant.now()
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class, UsernameNotFoundException::class, ProfileNotFoundException::class, WorkoutSessionNotFoundException::class)
    fun handleNotFound(ex: RuntimeException, request: HttpServletRequest) =
        error(HttpStatus.NOT_FOUND, ex.message ?: "Not found", request.requestURI)

    @ExceptionHandler(ProfileAlreadyExistsException::class, EmailAlreadyInUseException::class)
    fun handleConflict(ex: LiftrrException, request: HttpServletRequest) =
        error(HttpStatus.CONFLICT, ex.message ?: "Conflict", request.requestURI)

    @ExceptionHandler(InvalidObjectKeyException::class)
    fun handleForbidden(ex: InvalidObjectKeyException, request: HttpServletRequest) =
        error(HttpStatus.FORBIDDEN, ex.message ?: "Forbidden", request.requestURI)

    @ExceptionHandler(PhotoNotFoundException::class, MediaNotFoundException::class)
    fun handleUnprocessable(ex: LiftrrException, request: HttpServletRequest) =
        error(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Unprocessable", request.requestURI)

    @ExceptionHandler(LiftrrUnauthorizedException::class)
    fun handleUnauthorized(ex: LiftrrUnauthorizedException, request: HttpServletRequest) =
        error(HttpStatus.UNAUTHORIZED, ex.message ?: "Unauthorized", request.requestURI)

    @ExceptionHandler(UserNotPersistedException::class)
    fun handleServerError(ex: UserNotPersistedException, request: HttpServletRequest) =
        error(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "Internal error", request.requestURI)

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.valueOf(ex.statusCode.value())
        return error(status, ex.reason ?: ex.message, request.requestURI)
    }
}
