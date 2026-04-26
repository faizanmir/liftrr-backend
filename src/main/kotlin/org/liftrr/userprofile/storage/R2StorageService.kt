package org.liftrr.userprofile.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.Duration
import java.util.UUID

@Service
class R2StorageService(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    @param:Value("\${r2.bucket}") private val bucket: String,
    @param:Value("\${r2.public-url}") private val publicUrl: String
) {

    companion object {
        private val PRESIGN_DURATION: Duration = Duration.ofMinutes(15)
    }

    fun generateProfilePhotoUploadUrl(userId: UUID): UploadTarget {
        val objectKey = "profiles/$userId/${UUID.randomUUID()}"

        val presignedUrl = s3Presigner.presignPutObject(
            PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGN_DURATION)
                .putObjectRequest(
                    PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType("image/*")
                        .build()
                )
                .build()
        ).url().toString()

        return UploadTarget(
            presignedUploadUrl = presignedUrl,
            publicUrl = "$publicUrl/$objectKey",
            objectKey = objectKey
        )
    }

    fun objectExists(objectKey: String): Boolean = try {
        s3Client.headObject { it.bucket(bucket).key(objectKey) }
        true
    } catch (_: Exception) {
        false
    }

    fun publicUrlFor(objectKey: String): String = "$publicUrl/$objectKey"

    fun deleteProfilePhoto(photoUrl: String) {
        val objectKey = photoUrl.removePrefix("$publicUrl/")
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build()
        )
    }
}

data class UploadTarget(
    val presignedUploadUrl: String,
    val publicUrl: String,
    val objectKey: String
)
