package org.liftrr.storage

interface StorageService {
    fun generateUploadUrl(objectKey: String, contentType: String): UploadTarget
    fun objectExists(objectKey: String): Boolean
    fun publicUrlFor(objectKey: String): String
    fun objectKeyFromUrl(url: String): String
    fun delete(objectKey: String)
}
