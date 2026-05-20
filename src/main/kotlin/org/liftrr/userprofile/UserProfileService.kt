package org.liftrr.userprofile

import org.liftrr.common.ProfileAlreadyExistsException
import org.liftrr.common.ProfileNotFoundException
import org.liftrr.user.UserService
import org.liftrr.userprofile.dto.UserProfileRequest
import org.liftrr.userprofile.dto.UserProfileResponse
import org.liftrr.storage.StorageService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository,
    private val userService: UserService,
    @Qualifier("photoStorage") private val storageService: StorageService
) {

    fun createProfile(email: String, request: UserProfileRequest): UserProfileResponse {
        val (user, userId) = userService.resolveUser(email)

        val objectKey = "profiles/$userId/${UUID.randomUUID()}"
        val uploadTarget = storageService.generateUploadUrl(objectKey, "image/*")

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
        val (_, userId) = userService.resolveUser(email)
        return userProfileRepository.findByUserId(userId) ?: throw ProfileNotFoundException(userId)
    }

    fun editProfile(email: String, request: UserProfileRequest): UserProfile {
        val (_, userId) = userService.resolveUser(email)

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw ProfileNotFoundException(userId)

        profile.firstName = request.firstName ?: profile.firstName
        profile.lastName = request.lastName ?: profile.lastName
        return userProfileRepository.save(profile)
    }

    fun deleteProfile(email: String) {
        val (_, userId) = userService.resolveUser(email)

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw ProfileNotFoundException(userId)

        userProfileRepository.delete(profile)
    }
}
