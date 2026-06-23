package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.Serializable

@Serializable
data class V3ContentPageDto(
    val pageType: GraphNodeType = GraphNodeType.Unknown,
    val content: ContentRefDto? = null,
    val digest: ContentDigestSectionDto? = null,
    val aiCard: AiCardDto? = null,
    val graph: GraphNeighborsDto? = null,
    val recommendations: List<ContentRefDto> = emptyList(),
    val relatedContent: List<ContentRefDto> = emptyList(),
    val localState: LocalStateSectionDto? = null,
    val exportHints: List<String> = emptyList(),
    val sectionStatus: List<SectionStatusDto> = emptyList(),
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class SectionStatusDto(
    val section: String,
    val status: SectionStatus = SectionStatus.Unknown,
)

@Serializable
data class LocalStateSectionDto(
    val isFavorite: Boolean = false,
    val lastViewedAt: String? = null,
    val viewCount: Int = 0,
)

@Serializable
data class ContentDigestSectionDto(
    val summary: String? = null,
    val highlights: List<String> = emptyList(),
    val keyFacts: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
)

@Serializable
data class ContentIntelligenceDto(
    val type: GraphNodeType = GraphNodeType.Unknown,
    val id: String,
    val title: String? = null,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val category: String? = null,
    val region: String? = null,
    val year: Int? = null,
    val kind: String? = null,
    val projectCode: String? = null,
    val aiCard: AiCardDto? = null,
    val graph: GraphNeighborsDto? = null,
    val recommendations: List<ContentRefDto> = emptyList(),
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class AiCardDto(
    val targetType: String? = null,
    val targetId: String? = null,
    val taskType: String? = null,
    val hasAi: Boolean = false,
    val status: String? = null,
    val summary: String? = null,
    val shortSummary: String? = null,
    val keywords: List<String> = emptyList(),
    val highlights: List<String> = emptyList(),
    val entities: List<AiEntityDto> = emptyList(),
    val modelName: String? = null,
    val promptVersion: String? = null,
    val sourceHash: String? = null,
    val generatedAt: String? = null,
    val updatedAt: String? = null,
    val expiresAt: String? = null,
    val isStale: Boolean = false,
    val staleReason: String? = null,
    val rawJson: String? = null,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class AiEntityDto(
    val name: String,
    val type: String? = null,
)

@Serializable
data class IntelligentSearchResponseDto(
    val items: List<IntelligentSearchItemDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val total: Long = 0,
    val hasMore: Boolean = false,
    val query: String? = null,
    val weights: IntelligentSearchWeightsDto? = null,
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class IntelligentSearchItemDto(
    val type: GraphNodeType = GraphNodeType.Unknown,
    val id: String,
    val title: String? = null,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val category: String? = null,
    val region: String? = null,
    val year: Int? = null,
    val kind: String? = null,
    val score: Double = 0.0,
    val scoreBreakdown: IntelligentSearchScoreBreakdownDto? = null,
    val matchedFields: List<String> = emptyList(),
    val snippets: List<String> = emptyList(),
    val aiSummary: String? = null,
    val aiKeywords: List<String> = emptyList(),
    val hasAi: Boolean = false,
    val isStale: Boolean = false,
)

@Serializable
data class IntelligentSearchScoreBreakdownDto(
    val titleExact: Double = 0.0,
    val titleToken: Double = 0.0,
    val metadataMatch: Double = 0.0,
    val summaryMatch: Double = 0.0,
    val aiSummaryMatch: Double = 0.0,
    val aiKeywordMatch: Double = 0.0,
    val aiHighlightMatch: Double = 0.0,
    val graphBoost: Double = 0.0,
    val freshAiBoost: Double = 0.0,
    val stalePenalty: Double = 0.0,
    val total: Double = 0.0,
)

@Serializable
data class IntelligentSearchWeightsDto(
    val ruleWeight: Double = 1.0,
    val graphWeight: Double = 1.0,
    val aiWeight: Double = 1.0,
)
