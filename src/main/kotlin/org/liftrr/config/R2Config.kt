package org.liftrr.config

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
    @param:Value("\${r2.secret-key}") private val secretKey: String
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
}
