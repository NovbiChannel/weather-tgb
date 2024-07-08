package org.novbicreate.domain

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.novbicreate.domain.ApiRoutes.POST_EVENT
import org.novbicreate.domain.models.EventData

suspend fun sendEvent(details: String, client: HttpClient) {
    client.post(POST_EVENT) {
        contentType(ContentType.Application.Json)
        setBody(
            EventData(
                type = "client",
                source = "weather_tg_bot",
                time = System.currentTimeMillis(),
                details = details
            )
        )
    }
}