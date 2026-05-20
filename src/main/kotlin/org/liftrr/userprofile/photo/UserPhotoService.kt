package org.liftrr.userprofile.photo

import org.liftrr.common.ProfileNotFoundException
import org.liftrr.storage.MediaUploadService
import org.liftrr.storage.StorageService
import org.liftrr.user.UserService
import org.liftrr.userprofile.UserProfileRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserPhotoService(
    private val userService: UserService,
    @Qualifier("photoStorage") private val storageService: StorageService,
    @Qualifier("photoMediaUpload") private val mediaUploadService: MediaUploadService,
    private val userProfileRepository: UserProfileRepository
) {

    companion object {
        private const val NAMESPACE = "profiles"
    }

    fun requestUploadUrl(email: String): PhotoUploadUrlResponse {
        val (_, userId) = userService.resolveUser(email)
        val uploadTarget = mediaUploadService.requestUpload("$NAMESPACE/$userId", "image/*")
        return PhotoUploadUrlResponse(
            presignedUploadUrl = uploadTarget.presignedUploadUrl,
            objectKey = uploadTarget.objectKey
        )
    }

    fun confirmUpload(email: String, objectKey: String) {
        val (_, userId) = userService.resolveUser(email)
        mediaUploadService.verifyUpload("$NAMESPACE/$userId", objectKey)

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw ProfileNotFoundException(userId)

        profile.photoUrl?.let { storageService.delete(storageService.objectKeyFromUrl(it)) }
        profile.photoUrl = storageService.publicUrlFor(objectKey)
        userProfileRepository.save(profile)
    }
}
