package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.ResearchArtifactQuery
import com.duckylife.heritage.modern.core.network.ResearchPackageDetailQuery
import com.duckylife.heritage.modern.core.network.ResearchReportByPackageQuery
import com.duckylife.heritage.modern.core.network.ResearchReportDetailQuery
import com.duckylife.heritage.modern.core.network.api.ResearchApi
import com.duckylife.heritage.modern.core.network.isAllowedArtifactName
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageArtifactDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportFindingDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.feature.research.model.ResearchArtifactUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchEvidenceUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchFindingUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageDetailUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportDetailUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportItemUiModel
import javax.inject.Inject

/**
 * 研究资料包与研究报告只读仓库。
 */
interface ResearchRepository {
    suspend fun getPackages(): List<ResearchPackageItemUiModel>
    suspend fun getPackageDetail(packageId: String): ResearchPackageDetailUiModel
    suspend fun getArtifactContent(packageId: String, artifactName: String): String
    suspend fun getReports(): List<ResearchReportItemUiModel>
    suspend fun getReportDetail(reportId: String): ResearchReportDetailUiModel
    suspend fun getReportByPackage(packageId: String): ResearchReportDetailUiModel
}

class DefaultResearchRepository @Inject constructor(
    private val api: ResearchApi,
) : ResearchRepository {

    override suspend fun getPackages(): List<ResearchPackageItemUiModel> =
        api.getResearchPackages().packages.map { it.toItemUiModel() }

    override suspend fun getPackageDetail(packageId: String): ResearchPackageDetailUiModel =
        api.getResearchPackageDetail(ResearchPackageDetailQuery(packageId = packageId)).toDetailUiModel()

    override suspend fun getArtifactContent(packageId: String, artifactName: String): String {
        require(isAllowedArtifactName(artifactName)) {
            "artifactName must be a safe file name (no path separators, control chars, or traversal, 1-128 chars)"
        }
        return api.getResearchArtifact(ResearchArtifactQuery(packageId = packageId, artifactName = artifactName))
    }

    override suspend fun getReports(): List<ResearchReportItemUiModel> =
        api.getResearchReports().reports.map { it.toItemUiModel() }

    override suspend fun getReportDetail(reportId: String): ResearchReportDetailUiModel =
        api.getResearchReportDetail(ResearchReportDetailQuery(reportId = reportId)).toDetailUiModel()

    override suspend fun getReportByPackage(packageId: String): ResearchReportDetailUiModel =
        api.getResearchReportByPackage(ResearchReportByPackageQuery(packageId = packageId)).toDetailUiModel()
}

// ---------------------------------------------------------------------------
// Package mappers
// ---------------------------------------------------------------------------

internal fun ResearchPackageDto.toItemUiModel(): ResearchPackageItemUiModel {
    val safeArtifacts = artifacts.filter { isAllowedArtifactName(it.name) }
    return ResearchPackageItemUiModel(
        packageId = packageId,
        title = resolvePackageTitle(),
        subtitle = resolvePackageSubtitle(),
        createdAt = createdAt,
        status = status,
        isClickable = status == ResearchTaskStatus.Succeeded,
        artifactCount = safeArtifacts.size,
        includesContent = request?.includeContent ?: true,
        includesEvidence = request?.includeEvidence ?: true,
        includesAiResults = request?.includeAiResults ?: false,
        includesAiInferred = request?.includeAiInferred ?: false,
    )
}

internal fun ResearchPackageDto.toDetailUiModel(): ResearchPackageDetailUiModel {
    val safeArtifacts = artifacts.filter { isAllowedArtifactName(it.name) }
    return ResearchPackageDetailUiModel(
        packageId = packageId,
        title = resolvePackageTitle(),
        querySummary = resolvePackageSubtitle(),
        source = request?.snapshotId?.takeIf { it.isNotBlank() }
            ?: graphRagPackId.takeIf { it.isNotBlank() },
        dataScope = buildDataScopeLabel(),
        createdAt = createdAt,
        status = status,
        nodeCount = safeArtifacts.count { it.artifactType == "content" },
        edgeCount = safeArtifacts.count { it.artifactType == "graph" },
        sourceCount = safeArtifacts.count { it.artifactType == "sources" },
        evidenceCount = safeArtifacts.count { it.artifactType == "evidence" },
        artifacts = safeArtifacts.map { it.toUiModel() },
        hasReport = false,
        reportId = null,
        warnings = warnings,
        includesContent = request?.includeContent ?: true,
        includesEvidence = request?.includeEvidence ?: true,
        includesAiResults = request?.includeAiResults ?: false,
        includesAiInferred = request?.includeAiInferred ?: false,
    )
}

internal fun ResearchPackageArtifactDto.toUiModel(): ResearchArtifactUiModel =
    ResearchArtifactUiModel(
        name = name,
        displayName = name,
        artifactType = artifactType,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        isViewable = isViewableArtifact(mimeType),
    )

private fun ResearchPackageDto.resolvePackageTitle(): String {
    if (!graphRagPackId.isBlank()) return graphRagPackId
    if (!snapshotId.isNullOrBlank()) return snapshotId
    return packageId
}

private fun ResearchPackageDto.resolvePackageSubtitle(): String? {
    val snapshot = request?.snapshotId?.takeIf { it.isNotBlank() }
    val parts = buildList {
        if (snapshot != null) add(snapshot)
        if (!graphRagPackId.isBlank() && snapshot == null) add(graphRagPackId)
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" · ")
}

private fun ResearchPackageDto.buildDataScopeLabel(): String {
    val flags = buildList {
        if (request?.includeContent != false) add("content")
        if (request?.includeEvidence != false) add("evidence")
        if (request?.includeAiResults == true) add("aiResults")
        if (request?.includeAiInferred == true) add("aiInferred")
    }
    return flags.joinToString(", ")
}

private fun isViewableArtifact(mimeType: String): Boolean =
    mimeType.startsWith("text/") ||
        mimeType == "application/json" ||
        mimeType == "application/markdown"

// ---------------------------------------------------------------------------
// Report mappers
// ---------------------------------------------------------------------------

internal fun ResearchReportDto.toItemUiModel(): ResearchReportItemUiModel =
    ResearchReportItemUiModel(
        reportId = reportId,
        packageId = packageId.takeIf { it.isNotBlank() },
        title = title.takeIf { it.isNotBlank() } ?: reportId,
        status = status,
        createdAt = createdAt,
        modelName = modelName.takeIf { it.isNotBlank() },
    )

internal fun ResearchReportDto.toDetailUiModel(): ResearchReportDetailUiModel =
    ResearchReportDetailUiModel(
        reportId = reportId,
        packageId = packageId.takeIf { it.isNotBlank() },
        title = title.takeIf { it.isNotBlank() } ?: reportId,
        status = status,
        createdAt = createdAt,
        executiveSummary = executiveSummary,
        findings = findings.mapIndexed { index, finding ->
            ResearchFindingUiModel(
                number = index + 1,
                title = finding.resolveFindingTitle(),
                body = finding.claim,
                confidence = finding.confidence.takeIf { it > 0 }?.coerceAtMost(1.0),
                evidence = finding.evidenceIds.map { evidenceId ->
                    ResearchEvidenceUiModel(
                        evidenceId = evidenceId,
                        title = evidenceId,
                    )
                },
                limitations = finding.limitations,
            )
        },
        sourceCount = findings.flatMap { it.evidenceIds }.distinct().size,
        limitations = limitations,
        warnings = warnings,
        followUpQuestions = followUpQuestions,
    )

private fun ResearchReportFindingDto.resolveFindingTitle(): String? {
    val firstLine = claim.lines().firstOrNull()?.trim().orEmpty()
    return firstLine.takeIf { it.isNotBlank() }?.take(80)
}
