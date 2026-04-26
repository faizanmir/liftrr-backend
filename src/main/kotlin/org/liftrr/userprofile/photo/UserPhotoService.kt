package org.liftrr.userprofile.photo

import org.liftrr.user.UserRepository
import org.liftrr.userprofile.UserProfileRepository
import org.liftrr.userprofile.storage.R2StorageService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserPhotoService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val r2StorageService: R2StorageService
) {

    fun requestUploadUrl(email: String): PhotoUploadUrlResponse {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        userProfileRepository.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")

        val uploadTarget = r2StorageService.generateProfilePhotoUploadUrl(user.id)

        return PhotoUploadUrlResponse(
            presignedUploadUrl = uploadTarget.presignedUploadUrl,
            objectKey = uploadTarget.objectKey
        )
    }

    fun confirmUpload(email: String, objectKey: String) {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if (!objectKey.startsWith("profiles/${user.id}/")) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid object key")
        }

        if (!r2StorageService.objectExists(objectKey)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Photo not found in storage — upload may have failed")
        }

        val profile = userProfileRepository.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")

        profile.photoUrl?.let { r2StorageService.deleteProfilePhoto(it) }
        profile.photoUrl = r2StorageService.publicUrlFor(objectKey)
        userProfileRepository.save(profile)
    }
}
