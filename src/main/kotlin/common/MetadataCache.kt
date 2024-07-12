package org.novbicreate.common

import eu.vendeli.tgbot.types.User
import org.novbicreate.domain.ApiRepository
import org.novbicreate.domain.models.Metadata

class MetadataCache(private val repository: Lazy<ApiRepository>) {
    private var metadata: Metadata? = null

    suspend fun initialize(user: User) {
        if (metadata == null) {
            metadata = repository.value.getMetaData(user.languageCode)
        }
    }

    fun getMetadata(): Metadata {
        return metadata ?: throw IllegalStateException("Metadata not initialized")
    }
}