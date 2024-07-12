package org.novbicreate.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val welcomeMessage: String,
    val additionalMessage: String,
    val connectionErrorMessage: String,
    val timeoutErrorMessage: String,
    val illegalArgumentErrorMessage: String,
    val unknownError: String,
    val weatherTitle: String,
    val humidity: String,
    val wind: String,
    val ms: String,
)