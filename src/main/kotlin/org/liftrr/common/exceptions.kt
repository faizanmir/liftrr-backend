package org.liftrr.common

import java.util.UUID

sealed class LiftrrException(message: String) : RuntimeException(message)

// 404
class UserNotFoundException(email: String) : LiftrrException("User not found: $email")
class ProfileNotFoundException(userId: UUID) : LiftrrException("Profile not found for user: $userId")
class WorkoutSessionNotFoundException(sessionId: UUID) : LiftrrException("Workout session not found: $sessionId")

// 409
class ProfileAlreadyExistsException(userId: UUID) : LiftrrException("Profile already exists for user: $userId")
class EmailAlreadyInUseException(email: String) : LiftrrException("Email already in use: $email")

// 403
class InvalidObjectKeyException(objectKey: String) : LiftrrException("Invalid object key: $objectKey")

// 422
class PhotoNotFoundException(objectKey: String) : LiftrrException("Photo not found in storage: $objectKey")
class MediaNotFoundException(objectKey: String) : LiftrrException("Media not found in storage: $objectKey")

// 500
class UserNotPersistedException : LiftrrException("User exists but has no persisted ID")

// 401
sealed class LiftrrUnauthorizedException(message: String) : LiftrrException(message)
class InvalidRefreshTokenException : LiftrrUnauthorizedException("Refresh token not found")
class ExpiredRefreshTokenException : LiftrrUnauthorizedException("Refresh token expired")
class ReplayedRefreshTokenException : LiftrrUnauthorizedException("Refresh token already used — possible replay attack")
class InvalidGoogleTokenException : LiftrrUnauthorizedException("Invalid Google token")
