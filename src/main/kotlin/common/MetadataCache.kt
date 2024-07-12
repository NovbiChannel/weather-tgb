package org.novbicreate.common

import eu.vendeli.tgbot.types.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.novbicreate.domain.ApiRoutes
import org.novbicreate.domain.models.Metadata

class MetadataCache(private val client: HttpClient) {
    private var metadata: Metadata? = null

    suspend fun initialize(user: User) {
        if (metadata == null) {
            metadata = getMeta(user.languageCode)
        }
    }

    fun getMetadata(): Metadata {
        return metadata ?: throw IllegalStateException("Metadata not initialized")
    }

    private suspend fun getMeta(lang: String?): Metadata? {
        return try {
            client.get(ApiRoutes.GET_METADATA) {
                parameter("language", lang)
            }.body<Metadata>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}