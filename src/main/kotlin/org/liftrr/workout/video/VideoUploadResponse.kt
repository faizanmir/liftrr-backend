package org.liftrr.workout.video

data class VideoUploadResponse(
    val presignedUploadUrl: String,
    val objectKey: String
)
