package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.Serializable

/**
 * 研究资料包列表响应。
 */
@Serializable
data class ResearchPackageListResultDto(
    val packages: List<ResearchPackageDto> = emptyList(),
    val totalCount: Int = 0,
)

/**
 * 研究资料包详情 DTO，与后端 `ResearchPackageDto` 对齐。
 */
@Serializable
data class ResearchPackageDto(
    val packageId: String,
    val status: ResearchTaskStatus = ResearchTaskStatus.Unknown,
    val request: ResearchPackageCreateRequestDto? = null,
    val graphRagPackId: String = "",
    val graphRagPackHash: String = "",
    val snapshotId: String? = null,
    val manifestHash: String = "",
    val artifacts: List<ResearchPackageArtifactDto> = emptyList(),
    val warnings: List<String> = emptyList(),
    val errorCode: String? = null,
    val createdAt: String? = null,
    val startedAt: String? = null,
    val completedAt: String? = null,
    val updatedAt: String? = null,
)

/**
 * 研究资料包创建请求（只读展示，App 不发起创建）。
 */
@Serializable
data class ResearchPackageCreateRequestDto(
    val graphRagPackId: String = "",
    val snapshotId: String? = null,
    val formats: List<String> = emptyList(),
    val includeContent: Boolean = true,
    val includeEvidence: Boolean = true,
    val includeAiResults: Boolean = false,
    val includeAiInferred: Boolean = false,
    val maxItems: Int = 100,
)

/**
 * 资料包 artifact 元数据。
 */
@Serializable
data class ResearchPackageArtifactDto(
    val name: String,
    val artifactType: String = "",
    val mimeType: String = "",
    val sizeBytes: Long = 0,
    val sha256: String = "",
)

/**
 * 研究报告列表响应。
 */
@Serializable
data class ResearchReportListResultDto(
    val reports: List<ResearchReportDto> = emptyList(),
    val totalCount: Int = 0,
)

/**
 * 研究报告详情 DTO，与后端 `ResearchReportDto` 对齐。
 */
@Serializable
data class ResearchReportDto(
    val reportId: String,
    val packageId: String = "",
    val packageManifestHash: String = "",
    val aiResultId: String? = null,
    val jobId: String? = null,
    val status: ResearchTaskStatus = ResearchTaskStatus.Unknown,
    val modelProvider: String = "",
    val modelName: String = "",
    val modelVersion: String = "",
    val promptVersion: String = "",
    val sourceHash: String = "",
    val resultHash: String = "",
    val title: String = "",
    val executiveSummary: String = "",
    val findings: List<ResearchReportFindingDto> = emptyList(),
    val followUpQuestions: List<String> = emptyList(),
    val limitations: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val errorCode: String? = null,
    val createdAt: String? = null,
    val generatedAt: String? = null,
    val updatedAt: String? = null,
)

/**
 * 研究发现。
 */
@Serializable
data class ResearchReportFindingDto(
    val findingId: String = "",
    val claim: String = "",
    val evidenceIds: List<String> = emptyList(),
    val confidence: Double = 0.0,
    val limitations: List<String> = emptyList(),
)
