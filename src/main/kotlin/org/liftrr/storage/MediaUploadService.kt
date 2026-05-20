package org.liftrr.storage

import org.liftrr.common.InvalidObjectKeyException
import org.liftrr.common.MediaNotFoundException
import java.util.UUID

class MediaUploadService(private val storageService: StorageService) {

    fun requestUpload(keyPrefix: String, contentType: String): UploadTarget {
        val objectKey = "$keyPrefix/${UUID.randomUUID()}"
        return storageService.generateUploadUrl(objectKey, contentType)
    }

    fun verifyUpload(keyPrefix: String, objectKey: String) {
        if (!objectKey.startsWith("$keyPrefix/")) {
            throw InvalidObjectKeyException(objectKey)
        }
        if (!storageService.objectExists(objectKey)) {
            throw MediaNotFoundException(objectKey)
        }
    }
}
