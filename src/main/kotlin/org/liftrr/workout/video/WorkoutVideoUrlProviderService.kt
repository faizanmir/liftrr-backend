package org.liftrr.workout.video

import org.liftrr.common.InvalidObjectKeyException
import org.liftrr.storage.MediaUploadService
import org.liftrr.storage.StorageService
import org.liftrr.user.UserService
import org.liftrr.workout.WorkoutSessionService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WorkoutVideoUrlProviderService(
    private val userService: UserService,
    private val workoutSessionService: WorkoutSessionService,
    @Qualifier("videoMediaUpload") private val mediaUploadService: MediaUploadService,
    @Qualifier("videoStorage") private val storageService: StorageService
) {

    companion object {
        private const val NAMESPACE = "workouts"
    }

    fun requestUploadUrl(email: String, sessionId: UUID): VideoUploadResponse {
        val (_, userId) = userService.resolveUser(email)
        workoutSessionService.getSessionForUser(sessionId, userId)
        val uploadTarget = mediaUploadService.requestUpload("$NAMESPACE/$userId/$sessionId", "video/*")
        return VideoUploadResponse(
            presignedUploadUrl = uploadTarget.presignedUploadUrl,
            objectKey = uploadTarget.objectKey
        )
    }

    fun confirmUpload(email: String, objectKey: String) {
        val (_, userId) = userService.resolveUser(email)

        // key format: workouts/{userId}/{sessionId}/{uuid}
        val parts = objectKey.split("/")
        if (parts.size != 4 || parts[0] != NAMESPACE || parts[1] != userId.toString()) {
            throw InvalidObjectKeyException(objectKey)
        }
        val sessionId = runCatching { UUID.fromString(parts[2]) }.getOrElse {
            throw InvalidObjectKeyException(objectKey)
        }

        mediaUploadService.verifyUpload("$NAMESPACE/$userId/$sessionId", objectKey)

        val session = workoutSessionService.getSessionForUser(sessionId, userId)
        session.videoCloudUrl = storageService.publicUrlFor(objectKey)
        workoutSessionService.saveVideoUrl(session)
    }
}
