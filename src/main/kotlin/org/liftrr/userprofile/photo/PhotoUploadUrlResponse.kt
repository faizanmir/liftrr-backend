package org.liftrr.userprofile.photo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Presigned URL response for photo upload")
data class PhotoUploadUrlResponse(
    @param:Schema(description = "PUT this URL with the image bytes to upload to storage") val presignedUploadUrl: String,
    @param:Schema(description = "Pass this key to PATCH /profile/photo to confirm the upload") val objectKey: String
)
