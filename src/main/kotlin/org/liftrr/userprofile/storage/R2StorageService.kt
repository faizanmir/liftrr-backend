package org.liftrr.userprofile.storage

import org.liftrr.storage.StorageService
import org.liftrr.storage.UploadTarget
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

class R2StorageService(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val bucket: String,
    private val publicUrl: String
) : StorageService {

    companion object {
        private val PRESIGN_DURATION: Duration = Duration.ofMinutes(15)
    }

    override fun generateUploadUrl(objectKey: String, contentType: String): UploadTarget {
        val presignedUrl = s3Presigner.presignPutObject(
            PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGN_DURATION)
                .putObjectRequest(
                    PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType(contentType)
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

    override fun objectExists(objectKey: String): Boolean = try {
        s3Client.headObject { it.bucket(bucket).key(objectKey) }
        true
    } catch (_: Exception) {
        false
    }

    override fun publicUrlFor(objectKey: String): String = "$publicUrl/$objectKey"

    override fun objectKeyFromUrl(url: String): String = url.removePrefix("$publicUrl/")

    override fun delete(objectKey: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build()
        )
    }
}
