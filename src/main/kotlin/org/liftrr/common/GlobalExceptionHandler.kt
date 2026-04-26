package org.liftrr.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: Instant = Instant.now()
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.valueOf(ex.statusCode.value())
        return ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = ex.reason ?: ex.message,
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFound(
        ex: UsernameNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = HttpStatus.NOT_FOUND.reasonPhrase,
                message = ex.message ?: "User not found",
                path = request.requestURI
            )
        )

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                message = "An unexpected error occurred",
                path = request.requestURI
            )
        )
}
