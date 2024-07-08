package org.novbicreate.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorData(
    val type: String,
    val source: String,
    val time: Long,
    val description: String,
)
