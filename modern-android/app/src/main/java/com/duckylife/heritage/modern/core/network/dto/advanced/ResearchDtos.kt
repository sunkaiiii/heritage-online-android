package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.Serializable

@Serializable
data class ResearchPackageSummaryDto(
    val packageId: String,
    val title: String? = null,
    val querySummary: String? = null,
    val createdAt: String? = null,
    val status: ResearchTaskStatus = ResearchTaskStatus.Unknown,
    val contentCount: Int = 0,
    val hasEvidence: Boolean = false,
    val hasAiResults: Boolean = false,
)

@Serializable
data class ResearchPackageDetailDto(
    val packageId: String,
    val title: String? = null,
    val querySummary: String? = null,
    val source: String? = null,
    val dataScope: String? = null,
    val createdAt: String? = null,
    val status: ResearchTaskStatus = ResearchTaskStatus.Unknown,
    val nodeCount: Int = 0,
    val edgeCount: Int = 0,
    val sourceCount: Int = 0,
    val evidenceCount: Int = 0,
    val artifacts: List<ResearchArtifactDto> = emptyList(),
    val reportId: String? = null,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class ResearchArtifactDto(
    val name: String,
    val fileName: String? = null,
    val format: String? = null,
    val size: Long? = null,
)

@Serializable
data class ResearchReportSummaryDto(
    val reportId: String,
    val packageId: String? = null,
    val title: String? = null,
    val status: ResearchTaskStatus = ResearchTaskStatus.Unknown,
    val createdAt: String? = null,
    val modelName: String? = null,
)

@Serializable
data class ResearchReportDetailDto(
    val reportId: String,
    val packageId: String? = null,
    val title: String? = null,
    val status: ResearchTaskStatus = ResearchTaskStatus.Unknown,
    val createdAt: String? = null,
    val executiveSummary: String? = null,
    val findings: List<ResearchFindingDto> = emptyList(),
    val sourceCount: Int = 0,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class ResearchFindingDto(
    val number: Int = 0,
    val title: String? = null,
    val body: String? = null,
    val evidenceIds: List<String> = emptyList(),
    val evidenceReferences: List<ContentRefDto> = emptyList(),
)
