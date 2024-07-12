package org.novbicreate.domain

object ApiRoutes {
    private const val BASE_URL = "http://127.0.0.1:8080"
    private const val BASE_METRIC_SERVICE = "http://127.0.0.1:8081"
    const val GET_WEATHER = "$BASE_URL/weather"
    const val GET_METADATA = "$BASE_URL/bot-metadata"
    const val POST_ERROR = "$BASE_METRIC_SERVICE/error_collection"
    const val POST_EVENT = "$BASE_METRIC_SERVICE/event_collection"
}