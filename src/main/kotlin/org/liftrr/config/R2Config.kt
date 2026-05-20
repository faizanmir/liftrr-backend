package org.liftrr.config

import org.liftrr.storage.MediaUploadService
import org.liftrr.storage.StorageService
import org.liftrr.userprofile.storage.R2StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class R2Config(
    @param:Value("\${r2.account-id}") private val accountId: String,
    @param:Value("\${r2.access-key}") private val accessKey: String,
    @param:Value("\${r2.secret-key}") private val secretKey: String,
    @param:Value("\${r2.bucket}") private val photoBucket: String,
    @param:Value("\${r2.public-url}") private val photoPublicUrl: String,
    @param:Value("\${r2.video.bucket}") private val videoBucket: String,
    @param:Value("\${r2.video.public-url}") private val videoPublicUrl: String
) {

    private val endpoint: URI
        get() = URI.create("https://$accountId.r2.cloudflarestorage.com")

    private val credentials: StaticCredentialsProvider
        get() = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )

    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .endpointOverride(endpoint)
        .credentialsProvider(credentials)
        .region(Region.of("auto"))
        .build()

    @Bean
    fun s3Presigner(): S3Presigner = S3Presigner.builder()
        .endpointOverride(endpoint)
        .credentialsProvider(credentials)
        .region(Region.of("auto"))
        .build()

    @Bean("photoStorage")
    fun photoStorageService(s3Client: S3Client, s3Presigner: S3Presigner): StorageService =
        R2StorageService(s3Client, s3Presigner, photoBucket, photoPublicUrl)

    @Bean("videoStorage")
    fun videoStorageService(s3Client: S3Client, s3Presigner: S3Presigner): StorageService =
        R2StorageService(s3Client, s3Presigner, videoBucket, videoPublicUrl)

    @Bean("photoMediaUpload")
    fun photoMediaUploadService(@org.springframework.beans.factory.annotation.Qualifier("photoStorage") storage: StorageService): MediaUploadService =
        MediaUploadService(storage)

    @Bean("videoMediaUpload")
    fun videoMediaUploadService(@org.springframework.beans.factory.annotation.Qualifier("videoStorage") storage: StorageService): MediaUploadService =
        MediaUploadService(storage)
}
