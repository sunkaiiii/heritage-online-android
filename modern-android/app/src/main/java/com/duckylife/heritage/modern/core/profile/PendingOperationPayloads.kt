package com.duckylife.heritage.modern.core.profile

import kotlinx.serialization.Serializable

@Serializable
data class AddFavoritePayload(
    val targetType: String,
    val targetId: String,
    val tags: List<String> = emptyList(),
    val note: String? = null,
)

@Serializable
data class RemoveFavoritePayload(
    val targetType: String,
    val targetId: String,
)

@Serializable
data class RecordHistoryPayload(
    val targetType: String,
    val targetId: String,
    val lastPosition: String? = null,
)

@Serializable
data class UpdateProgressPayload(
    val routeId: String,
    val completedStepIds: List<String> = emptyList(),
    val currentStepId: String? = null,
)

@Serializable
data class ClearHistoryPayload(
    val placeholder: Boolean = true,
)
