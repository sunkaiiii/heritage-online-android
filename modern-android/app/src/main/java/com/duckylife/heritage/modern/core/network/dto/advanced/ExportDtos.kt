package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.Serializable

@Serializable
data class ExportTemplateDto(
    val templateId: String,
    val title: String? = null,
    val description: String? = null,
    val scopeType: ExportScopeType = ExportScopeType.Unknown,
    val defaultFormat: ExportFormat = ExportFormat.Unknown,
    val supportedFormats: List<ExportFormat> = emptyList(),
    val defaultLimit: Int = 20,
    val maxLimit: Int = 500,
)

@Serializable
data class ExportPreviewDto(
    val scopeType: ExportScopeType = ExportScopeType.Unknown,
    val format: ExportFormat = ExportFormat.Unknown,
    val estimatedItemCount: Int = 0,
    val estimatedBytes: Long = 0,
    val warnings: List<String> = emptyList(),
    val sampleItems: List<ExportSampleItemDto> = emptyList(),
)

@Serializable
data class ExportSampleItemDto(
    val targetType: String? = null,
    val targetId: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val sourceUrl: String? = null,
    val summary: String? = null,
    val aiSummary: String? = null,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val kind: String? = null,
)

@Serializable
data class ExportContentResultDto(
    val fileName: String? = null,
    val mimeType: String? = null,
    val format: ExportFormat = ExportFormat.Unknown,
    val itemCount: Int = 0,
    val content: String? = null,
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class ExportRequestDto(
    val scopeType: ExportScopeType = ExportScopeType.Unknown,
    val format: ExportFormat = ExportFormat.Markdown,
    val targetType: String? = "all",
    val ids: List<String> = emptyList(),
    val query: String? = null,
    val topicType: String? = null,
    val topicKey: String? = null,
    val rankingId: String? = null,
    val routeId: String? = null,
    val profileId: String? = null,
    val includeAiSummary: Boolean = false,
    val includeSources: Boolean = true,
    val includeImages: Boolean = false,
    val limit: Int = 20,
) {
    init {
        require(scopeType != ExportScopeType.Unknown) { "scopeType must be specified" }
        require(format != ExportFormat.Unknown) { "format must be specified" }
        require(limit in 1..10_000) { "limit must be in 1..10000" }
        if (scopeType == ExportScopeType.Ids) {
            require(ids.isNotEmpty()) { "ids must not be empty when scopeType is Ids" }
        }
        if (scopeType == ExportScopeType.Search) {
            require(!query.isNullOrBlank()) { "query must not be blank when scopeType is Search" }
        }
        if (scopeType == ExportScopeType.Topic) {
            require(!topicType.isNullOrBlank()) { "topicType must not be blank when scopeType is Topic" }
            require(!topicKey.isNullOrBlank()) { "topicKey must not be blank when scopeType is Topic" }
        }
        if (scopeType == ExportScopeType.Ranking) {
            require(!rankingId.isNullOrBlank()) { "rankingId must not be blank when scopeType is Ranking" }
        }
        if (scopeType == ExportScopeType.LearningRoute) {
            require(!routeId.isNullOrBlank()) { "routeId must not be blank when scopeType is LearningRoute" }
        }
    }
}
