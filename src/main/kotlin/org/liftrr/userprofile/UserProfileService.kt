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
        if (userProfileRepository.findByUserId(userId) != null) {
            throw ProfileAlreadyExistsException(userId)
        }

        val objectKey = "profiles/$userId/${UUID.randomUUID()}"
        val uploadTarget = storageService.generateUploadUrl(objectKey, "image/*")

        val profile = userProfileRepository.save(
            UserProfile(
                user = user,
                firstName = request.firstName,
                lastName = request.lastName,
                photoUrl = request.photoUrl,
                gender = request.gender,
                height = request.height,
                fitnessLevel = request.fitnessLevel,
                dateOfBirth = request.dateOfBirth,
                weight = request.weight,
                goalsJson = request.goalsJson,
                preferredExercises = request.preferredExercises,
                preferredUnits = request.preferredUnits,
                notificationsEnabled = request.notificationsEnabled ?: true,
                reminderTime = request.reminderTime,
            )
        )

        return profile.toResponse(userId, user.email, uploadTarget.presignedUploadUrl)
    }

    fun fetchProfile(email: String): UserProfileResponse {
        val (user, userId) = userService.resolveUser(email)
        return (userProfileRepository.findByUserId(userId) ?: throw ProfileNotFoundException(userId))
            .toResponse(userId, user.email)
    }

    fun editProfile(email: String, request: UserProfileRequest): UserProfileResponse {
        val (user, userId) = userService.resolveUser(email)

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw ProfileNotFoundException(userId)

        profile.firstName = request.firstName ?: profile.firstName
        profile.lastName = request.lastName ?: profile.lastName
        profile.photoUrl = request.photoUrl ?: profile.photoUrl
        profile.gender = request.gender ?: profile.gender
        profile.height = request.height ?: profile.height
        profile.fitnessLevel = request.fitnessLevel ?: profile.fitnessLevel
        profile.dateOfBirth = request.dateOfBirth ?: profile.dateOfBirth
        profile.weight = request.weight ?: profile.weight
        profile.goalsJson = request.goalsJson ?: profile.goalsJson
        profile.preferredExercises = request.preferredExercises ?: profile.preferredExercises
        profile.preferredUnits = request.preferredUnits ?: profile.preferredUnits
        profile.notificationsEnabled = request.notificationsEnabled ?: profile.notificationsEnabled
        profile.reminderTime = request.reminderTime ?: profile.reminderTime
        profile.modifiedAt = java.time.Instant.now()
        profile.lastSyncedAt = java.time.Instant.now()

        return userProfileRepository.save(profile).toResponse(userId, user.email)
    }

    fun deleteProfile(email: String) {
        val (_, userId) = userService.resolveUser(email)

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw ProfileNotFoundException(userId)

        userProfileRepository.delete(profile)
    }

    private fun UserProfile.toResponse(
        userId: UUID,
        email: String,
        photoUploadUrl: String? = null
    ): UserProfileResponse =
        UserProfileResponse(
            userId = userId.toString(),
            profileId = id?.toString(),
            email = email,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl,
            gender = gender,
            height = height,
            fitnessLevel = fitnessLevel,
            dateOfBirth = dateOfBirth,
            weight = weight,
            goalsJson = goalsJson,
            preferredExercises = preferredExercises,
            preferredUnits = preferredUnits,
            notificationsEnabled = notificationsEnabled,
            reminderTime = reminderTime,
            createdAt = createdAt.toString(),
            modifiedAt = modifiedAt.toString(),
            lastSyncedAt = lastSyncedAt?.toString(),
            version = version,
            photoUploadUrl = photoUploadUrl
        )
}
