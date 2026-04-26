package org.liftrr.userprofile

import org.liftrr.user.UserRepository
import org.liftrr.userprofile.dto.UserProfileRequest
import org.liftrr.userprofile.dto.UserProfileResponse
import org.liftrr.userprofile.storage.R2StorageService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository,
    private val userRepository: UserRepository,
    private val r2StorageService: R2StorageService
) {

    fun createProfile(email: String, request: UserProfileRequest): UserProfileResponse {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if (userProfileRepository.findByUserId(user.id) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists")
        }

        val uploadTarget = r2StorageService.generateProfilePhotoUploadUrl(user.id)

        userProfileRepository.save(
            UserProfile(
                user = user,
                firstName = request.firstName,
                lastName = request.lastName,
            )
        )

        return UserProfileResponse(
            success = true,
            photoUploadUrl = uploadTarget.presignedUploadUrl
        )
    }

    fun fetchProfile(email: String): UserProfile {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        return userProfileRepository.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")
    }

    fun editProfile(email: String, request: UserProfileRequest): UserProfile {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val profile = userProfileRepository.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")

        profile.firstName = request.firstName ?: profile.firstName
        profile.lastName = request.lastName ?: profile.lastName
        return userProfileRepository.save(profile)
    }

    fun deleteProfile(email: String) {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val profile = userProfileRepository.findByUserId(user.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")

        userProfileRepository.delete(profile)
    }
}
