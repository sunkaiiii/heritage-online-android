package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val source: String? = null,
    val relationType: String? = null,
    val reason: String? = null,
    val weight: Double = 0.0,
    val category: String? = null,
    val region: String? = null,
    val publishedAt: String? = null,
    val publishedYear: Int? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class GraphNodeDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val category: String? = null,
    val region: String? = null,
    val sourceUrl: String? = null,
    val subtitle: String? = null,
    val coverImage: MediaAssetDto? = null,
)

@Serializable
data class GraphEdgeDto(
    @SerialName("from") val fromId: String? = null,
    @SerialName("to") val toId: String? = null,
    val label: String? = null,
    val relationType: String? = null,
    val reason: String? = null,
    val source: String? = null,
    val weight: Double = 0.0,
)

@Serializable
data class GraphDto(
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
)

@Serializable
data class RelatedSummaryDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val region: String? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class ContextCollectionDto(
    val id: String? = null,
    val title: String? = null,
    val items: List<CollectionItemDto> = emptyList(),
)

@Serializable
data class DetailContextDto(
    val related: List<RelatedSummaryDto> = emptyList(),
    val graph: GraphDto? = null,
    val collections: List<ContextCollectionDto> = emptyList(),
    val exploreTopics: List<ExploreTopicInfoDto> = emptyList(),
    val recommendations: List<RecommendationDto> = emptyList(),
    val semanticRecommendations: List<RecommendationDto> = emptyList(),
)
