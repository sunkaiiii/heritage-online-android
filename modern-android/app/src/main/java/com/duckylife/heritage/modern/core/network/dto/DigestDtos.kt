package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContentDigestDto(
    val type: String = "",
    val id: String = "",
    val title: String = "",
    val quickRead: String? = null,
    val highlights: List<String> = emptyList(),
    val keyFacts: List<DigestFactDto> = emptyList(),
    val keywords: List<String> = emptyList(),
    val readingTimeMinutes: Int = 0,
    val sourceUrl: String = "",
    val generatedAt: String? = null,
)

@Serializable
data class DigestFactDto(
    val label: String = "",
    val value: String = "",
)
