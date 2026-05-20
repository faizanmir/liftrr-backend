package org.liftrr.storage

data class UploadTarget(
    val presignedUploadUrl: String,
    val publicUrl: String,
    val objectKey: String
)
