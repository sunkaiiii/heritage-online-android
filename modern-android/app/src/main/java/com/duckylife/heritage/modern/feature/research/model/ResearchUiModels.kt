package com.duckylife.heritage.modern.feature.research.model

import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus

/**
 * 研究资料包来源类型。
 */
enum class ResearchSourceType {
    GraphRagPack,
    Snapshot,
    Unknown,
}

/**
 * 研究资料包数据范围标记。
 */
enum class ResearchDataScope {
    Content,
    Evidence,
    AiResults,
    AiInferred,
}

/**
 * 研究资料包列表项 UI 模型。
 */
data class ResearchPackageItemUiModel(
    val packageId: String,
    val title: String,
    val subtitle: String?,
    val createdAt: String?,
    val status: ResearchTaskStatus,
    val isClickable: Boolean,
    val artifactCount: Int,
    val includesContent: Boolean,
    val includesEvidence: Boolean,
    val includesAiResults: Boolean,
    val includesAiInferred: Boolean,
)

/**
 * 研究资料包详情 UI 模型。
 */
data class ResearchPackageDetailUiModel(
    val packageId: String,
    val title: String,
    val querySummary: String?,
    val sourceType: ResearchSourceType,
    val sourceDetail: String?,
    val dataScope: List<ResearchDataScope>,
    val createdAt: String?,
    val status: ResearchTaskStatus,
    val nodeCount: Int,
    val edgeCount: Int,
    val sourceCount: Int,
    val evidenceCount: Int,
    val artifacts: List<ResearchArtifactUiModel>,
    val hasReport: Boolean,
    val reportId: String?,
    val warnings: List<String>,
    val filteredArtifactCount: Int = 0,
    val includesContent: Boolean = true,
    val includesEvidence: Boolean = true,
    val includesAiResults: Boolean = false,
    val includesAiInferred: Boolean = false,
)

/**
 * Artifact UI 模型。
 */
data class ResearchArtifactUiModel(
    val name: String,
    val displayName: String,
    val artifactType: String,
    val mimeType: String,
    val sizeBytes: Long,
    val isViewable: Boolean,
)

/**
 * 研究报告列表项 UI 模型。
 */
data class ResearchReportItemUiModel(
    val reportId: String,
    val packageId: String?,
    val title: String,
    val status: ResearchTaskStatus,
    val createdAt: String?,
    val modelName: String?,
)

/**
 * 研究报告详情 UI 模型。
 */
data class ResearchReportDetailUiModel(
    val reportId: String,
    val packageId: String?,
    val title: String,
    val status: ResearchTaskStatus,
    val createdAt: String?,
    val executiveSummary: String,
    val findings: List<ResearchFindingUiModel>,
    val sourceCount: Int,
    val limitations: List<String>,
    val warnings: List<String>,
    val followUpQuestions: List<String>,
)

/**
 * 研究发现 UI 模型。
 */
data class ResearchFindingUiModel(
    val number: Int,
    val title: String?,
    val body: String,
    val confidence: Double?,
    val evidence: List<ResearchEvidenceUiModel>,
    val limitations: List<String>,
)

/**
 * 研究证据引用 UI 模型。
 */
data class ResearchEvidenceUiModel(
    val evidenceId: String,
    val title: String?,
)
