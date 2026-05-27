package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ExploreTopicType(val wireName: String) {
    @SerialName("region")
    Region("region"),

    @SerialName("category")
    Category("category"),

    @SerialName("year")
    Year("year"),

    @SerialName("kind")
    Kind("kind"),
}

@Serializable
enum class ExploreTopicListType(val wireName: String) {
    @SerialName("region")
    Region("region"),

    @SerialName("category")
    Category("category"),

    @SerialName("year")
    Year("year"),

    @SerialName("mixed")
    Mixed("mixed"),

    @SerialName("all")
    All("all"),
}

@Serializable
data class ExploreTopicInfoDto(
    val type: String? = null,
    val key: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
)

@Serializable
data class ExploreTopicItemDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val kind: String? = null,
    val year: Int? = null,
    val count: Long = 0,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class ExploreTopicSectionDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val items: List<ExploreTopicItemDto> = emptyList(),
)

@Serializable
data class ExploreTopicStatDto(
    val name: String? = null,
    val value: Long = 0,
)

@Serializable
data class ExploreTopicV2Dto(
    val topic: ExploreTopicInfoDto? = null,
    val stats: List<ExploreTopicStatDto> = emptyList(),
    val sections: List<ExploreTopicSectionDto> = emptyList(),
    val relatedTopics: List<ExploreTopicLinkDto> = emptyList(),
    val timeline: List<ExploreTopicItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class ExploreIndexDto(
    val regions: List<ExploreTopicInfoDto> = emptyList(),
    val categories: List<ExploreTopicInfoDto> = emptyList(),
    val years: List<ExploreTopicInfoDto> = emptyList(),
)

@Serializable
data class LearningPathStepDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val topic: ExploreTopicInfoDto? = null,
    val items: List<ExploreTopicItemDto> = emptyList(),
)

@Serializable
data class LearningPathDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val topics: List<ExploreTopicLinkDto> = emptyList(),
    val description: String? = null,
    val coverImage: String? = null,
    val estimatedItemCount: Int = 0,
    val stepCount: Int = 0,
    val tags: List<String> = emptyList(),
)

@Serializable
data class LearningPathDetailDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val steps: List<LearningPathStepDto> = emptyList(),
    val featuredItems: List<ExploreTopicItemDto> = emptyList(),
    val relatedTopics: List<ExploreTopicLinkDto> = emptyList(),
    val generatedAt: String? = null,
)
