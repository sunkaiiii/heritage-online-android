@file:OptIn(ExperimentalMaterial3Api::class)

package com.duckylife.heritage.modern.feature.research

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.feature.research.model.ResearchArtifactUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageDetailUiModel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageErrorState
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.state.AsyncState
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.duckylife.heritage.modern.feature.research.model.ResearchDataScope
import com.duckylife.heritage.modern.feature.research.model.ResearchSourceType
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import com.duckylife.heritage.modern.ui.text.formatIsoDate
import com.duckylife.heritage.modern.ui.text.localizedResearchDataScopeList
import com.duckylife.heritage.modern.ui.text.localizedResearchSource

@Composable
fun ResearchPackageRoute(
    packageId: String,
    onBack: () -> Unit,
    onViewReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResearchPackageViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Route 参数通过 LaunchedEffect 设置，支持空 SavedStateHandle 场景。
    LaunchedEffect(packageId) {
        viewModel.setPackageId(packageId)
    }

    LaunchedEffect(Unit) {
        viewModel.shareEvent.collect { payload ->
            val intent = when (payload) {
                is SharePayload.Text -> Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, payload.content)
                }

                is SharePayload.File -> {
                    val authority = "${context.packageName}.fileprovider"
                    val uri = FileProvider.getUriForFile(context, authority, payload.file)
                    Intent(Intent.ACTION_SEND).apply {
                        type = payload.mimeType
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }
            val chooser = Intent.createChooser(intent, context.getString(R.string.research_package_artifact_share))
            try {
                context.startActivity(chooser)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    R.string.research_package_share_no_app,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    ResearchPackageScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onViewArtifact = viewModel::loadArtifact,
        onViewReport = onViewReport,
        onShareArtifact = viewModel::shareArtifact,
        modifier = modifier,
    )
}

@Composable
internal fun ResearchPackageScreen(
    uiState: ResearchPackageUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onViewArtifact: (String) -> Unit,
    onViewReport: (String) -> Unit,
    onShareArtifact: (ResearchArtifactUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val detail = uiState.detail
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.research_packages_tab)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        HeritagePageBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                detail.isLoading && detail.data == null -> PackageLoadingPlaceholder()
                detail.errorKind != null && detail.data == null -> HeritageErrorState(
                    errorKind = detail.errorKind,
                    onRetry = onRetry,
                    modifier = Modifier.padding(20.dp),
                )

                detail.data != null -> PackageDetailContent(
                    packageDetail = detail.data,
                    onViewArtifact = onViewArtifact,
                    onViewReport = onViewReport,
                    onShareArtifact = onShareArtifact,
                )
            }
        }
    }
}

@Composable
private fun PackageDetailContent(
    packageDetail: ResearchPackageDetailUiModel,
    onViewArtifact: (String) -> Unit,
    onViewReport: (String) -> Unit,
    onShareArtifact: (ResearchArtifactUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = packageDetail.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                PackageStatusLine(status = packageDetail.status)
                if (!packageDetail.createdAt.isNullOrBlank()) {
                    Text(
                        text = formatIsoDate(packageDetail.createdAt) ?: packageDetail.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = stringResource(
                        R.string.research_package_source_format,
                        localizedResearchSource(packageDetail.sourceType, packageDetail.sourceDetail),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (packageDetail.dataScope.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            R.string.research_package_scope_format,
                            localizedResearchDataScopeList(packageDetail.dataScope),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            ContentStatsSection(packageDetail = packageDetail)
        }

        if (packageDetail.hasReport && packageDetail.reportId != null) {
            item {
                Button(
                    onClick = { onViewReport(packageDetail.reportId) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(stringResource(R.string.research_package_view_report))
                }
            }
        }

        if (packageDetail.warnings.isNotEmpty() || packageDetail.filteredArtifactCount > 0) {
            item {
                val allWarnings = packageDetail.warnings.toMutableList().apply {
                    if (packageDetail.filteredArtifactCount > 0) {
                        add(
                            stringResource(
                                R.string.research_package_artifacts_filtered_warning,
                                packageDetail.filteredArtifactCount,
                            ),
                        )
                    }
                }
                WarningsSection(warnings = allWarnings)
            }
        }

        item {
            Text(
                text = stringResource(R.string.research_package_section_artifacts),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (packageDetail.artifacts.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.content_empty_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(packageDetail.artifacts, key = { it.name }) { artifact ->
                ArtifactCard(
                    artifact = artifact,
                    onView = { onViewArtifact(artifact.name) },
                    onShare = { onShareArtifact(artifact) },
                )
            }
        }
    }
}

@Composable
private fun PackageStatusLine(
    status: ResearchTaskStatus,
    modifier: Modifier = Modifier,
) {
    val color = when (status) {
        ResearchTaskStatus.Succeeded -> MaterialTheme.colorScheme.primary
        ResearchTaskStatus.Running, ResearchTaskStatus.Queued -> MaterialTheme.colorScheme.tertiary
        ResearchTaskStatus.Failed, ResearchTaskStatus.Cancelled -> MaterialTheme.colorScheme.error
        ResearchTaskStatus.Unknown -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = stringResource(
            when (status) {
                ResearchTaskStatus.Succeeded -> R.string.research_package_status_succeeded
                ResearchTaskStatus.Running -> R.string.research_package_status_running
                ResearchTaskStatus.Queued -> R.string.research_package_status_queued
                ResearchTaskStatus.Failed -> R.string.research_package_status_failed
                ResearchTaskStatus.Cancelled -> R.string.research_package_status_cancelled
                ResearchTaskStatus.Unknown -> R.string.research_package_status_unknown
            },
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}

@Composable
private fun ContentStatsSection(
    packageDetail: ResearchPackageDetailUiModel,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.research_package_section_content),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HeritageMetaChip(
                    text = stringResource(R.string.research_package_artifact_count_format, packageDetail.artifacts.size),
                )
                HeritageMetaChip(
                    text = stringResource(R.string.research_package_node_count_format, packageDetail.nodeCount),
                )
                HeritageMetaChip(
                    text = stringResource(R.string.research_package_edge_count_format, packageDetail.edgeCount),
                )
                HeritageMetaChip(
                    text = stringResource(R.string.research_package_source_count_format, packageDetail.sourceCount),
                )
                HeritageMetaChip(
                    text = stringResource(R.string.research_package_evidence_count_format, packageDetail.evidenceCount),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (packageDetail.includesContent) {
                    HeritageMetaChip(text = stringResource(R.string.research_package_includes_content))
                }
                if (packageDetail.includesEvidence) {
                    HeritageMetaChip(text = stringResource(R.string.research_package_includes_evidence))
                }
                if (packageDetail.includesAiResults) {
                    HeritageMetaChip(text = stringResource(R.string.research_package_includes_ai_results))
                }
                if (packageDetail.includesAiInferred) {
                    HeritageMetaChip(text = stringResource(R.string.research_package_includes_ai_inferred))
                }
            }
        }
    }
}

@Composable
private fun ArtifactCard(
    artifact: ResearchArtifactUiModel,
    onView: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = artifact.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = formatArtifactSize(artifact.sizeBytes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (artifact.isViewable) {
                    TextButton(onClick = onView) {
                        Text(stringResource(R.string.research_package_artifact_view))
                    }
                }
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.research_package_artifact_share),
                    )
                }
            }
        }
    }
}

@Composable
private fun WarningsSection(
    warnings: List<String>,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.research_report_warnings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            warnings.forEach { warning ->
                Text(
                    text = "• $warning",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PackageLoadingPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.content_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun formatArtifactSize(sizeBytes: Long): String {
    val kb = sizeBytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        stringResource(R.string.research_artifact_size_format, mb)
    } else {
        stringResource(R.string.research_artifact_size_kb_format, kb.coerceAtLeast(1.0))
    }
}

@Preview(name = "Research Package - Detail")
@Composable
private fun ResearchPackageScreenPreview() {
    HeritageTheme {
        ResearchPackageScreen(
            uiState = ResearchPackageUiState(
                packageId = "pkg-1",
                detail = AsyncState(
                    data = ResearchPackageDetailUiModel(
                        packageId = "pkg-1",
                        title = "传统技艺传承调研包",
                        querySummary = "传统技艺、传承人、地区分布",
                        sourceType = ResearchSourceType.GraphRagPack,
                        sourceDetail = "GraphRAG",
                        dataScope = listOf(ResearchDataScope.Content, ResearchDataScope.Evidence),
                        createdAt = "2026-06-20T10:00:00Z",
                        status = ResearchTaskStatus.Succeeded,
                        nodeCount = 128,
                        edgeCount = 342,
                        sourceCount = 56,
                        evidenceCount = 89,
                        artifacts = listOf(
                            ResearchArtifactUiModel(
                                name = "summary.md",
                                displayName = "研究摘要",
                                artifactType = "markdown",
                                mimeType = "text/markdown",
                                sizeBytes = 12_345,
                                isViewable = true,
                            ),
                            ResearchArtifactUiModel(
                                name = "graph.json",
                                displayName = "知识图谱数据",
                                artifactType = "json",
                                mimeType = "application/json",
                                sizeBytes = 1_048_576,
                                isViewable = true,
                            ),
                        ),
                        hasReport = true,
                        reportId = "rep-1",
                        warnings = listOf("部分来源置信度较低"),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
            onViewArtifact = {},
            onViewReport = {},
            onShareArtifact = {},
        )
    }
}

@Preview(name = "Research Package - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResearchPackageScreenDarkPreview() {
    HeritageTheme {
        ResearchPackageScreen(
            uiState = ResearchPackageUiState(
                packageId = "pkg-1",
                detail = AsyncState(
                    data = ResearchPackageDetailUiModel(
                        packageId = "pkg-1",
                        title = "传统技艺传承调研包",
                        querySummary = null,
                        sourceType = ResearchSourceType.Snapshot,
                        sourceDetail = "快照导出",
                        dataScope = listOf(ResearchDataScope.Content),
                        createdAt = "2026-06-20T10:00:00Z",
                        status = ResearchTaskStatus.Succeeded,
                        nodeCount = 128,
                        edgeCount = 342,
                        sourceCount = 56,
                        evidenceCount = 89,
                        artifacts = listOf(
                            ResearchArtifactUiModel(
                                name = "summary.md",
                                displayName = "研究摘要",
                                artifactType = "markdown",
                                mimeType = "text/markdown",
                                sizeBytes = 12_345,
                                isViewable = true,
                            ),
                        ),
                        hasReport = false,
                        reportId = null,
                        warnings = emptyList(),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
            onViewArtifact = {},
            onViewReport = {},
            onShareArtifact = {},
        )
    }
}

@Preview(name = "Research Package - Loading")
@Composable
private fun ResearchPackageScreenLoadingPreview() {
    HeritageTheme {
        ResearchPackageScreen(
            uiState = ResearchPackageUiState(
                packageId = "pkg-1",
                detail = AsyncState(isLoading = true),
            ),
            onBack = {},
            onRetry = {},
            onViewArtifact = {},
            onViewReport = {},
            onShareArtifact = {},
        )
    }
}
