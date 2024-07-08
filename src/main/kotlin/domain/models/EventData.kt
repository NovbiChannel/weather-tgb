package org.novbicreate.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class EventData(
    val type: String,
    val source: String,
    val time: Long,
    val details: String,
)
