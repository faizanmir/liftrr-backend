package org.liftrr.userprofile.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User profile creation/update payload")
data class UserProfileRequest(
    @param:Schema(description = "First name", example = "John") val firstName: String?,
    @param:Schema(description = "Last name", example = "Doe") val lastName: String?,
    @param:Schema(description = "Profile photo URL") val photoUrl: String?,
    @param:Schema(description = "Gender value from the mobile profile model") val gender: String?,
    @param:Schema(description = "Height in centimeters", example = "180.0") val height: Float?,
    @param:Schema(description = "Fitness level value from the mobile profile model") val fitnessLevel: String?,
    @param:Schema(description = "Date of birth as epoch milliseconds") val dateOfBirth: Long?,
    @param:Schema(description = "Body weight in kilograms") val weight: Float?,
    @param:Schema(description = "JSON array of selected fitness goals") val goalsJson: String?,
    @param:Schema(description = "Preferred exercise value from the mobile profile model") val preferredExercises: String?,
    @param:Schema(description = "Preferred unit system value from the mobile profile model") val preferredUnits: String?,
    @param:Schema(description = "Whether reminders/notifications are enabled") val notificationsEnabled: Boolean?,
    @param:Schema(description = "Reminder time such as 09:00") val reminderTime: String?
)
