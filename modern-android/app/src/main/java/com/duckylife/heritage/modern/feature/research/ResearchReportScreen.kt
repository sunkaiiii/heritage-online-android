@file:OptIn(ExperimentalMaterial3Api::class)

package com.duckylife.heritage.modern.feature.research

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.feature.research.model.ResearchFindingUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportDetailUiModel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageErrorState
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.state.AsyncState

@Composable
fun ResearchReportRoute(
    reportId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResearchReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(reportId) {
        viewModel.setReportId(reportId)
    }

    ResearchReportScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
internal fun ResearchReportScreen(
    uiState: ResearchReportUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val detail = uiState.detail
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.research_report_title)) },
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
                detail.isLoading && detail.data == null -> ReportLoadingPlaceholder()
                detail.errorKind != null && detail.data == null -> HeritageErrorState(
                    errorKind = detail.errorKind,
                    onRetry = onRetry,
                    modifier = Modifier.padding(20.dp),
                )

                detail.data != null -> ReportDetailContent(
                    report = detail.data,
                    onBack = onBack,
                )
            }
        }
    }
}

@Composable
private fun ReportDetailContent(
    report: ResearchReportDetailUiModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                ReportStatusLine(status = report.status)
                if (!report.createdAt.isNullOrBlank()) {
                    Text(
                        text = report.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (report.status != ResearchTaskStatus.Succeeded) {
            item {
                NotReadyCard(onBack = onBack)
            }
        } else {
            if (report.executiveSummary.isNotBlank()) {
                item {
                    SummarySection(summary = report.executiveSummary)
                }
            }

            if (report.findings.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.research_report_findings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                itemsIndexed(report.findings, key = { _, finding -> finding.number }) { _, finding ->
                    FindingCard(finding = finding)
                }
            }

            if (report.limitations.isNotEmpty() || report.warnings.isNotEmpty()) {
                item {
                    LimitationsSection(
                        limitations = report.limitations,
                        warnings = report.warnings,
                    )
                }
            }

            if (report.followUpQuestions.isNotEmpty()) {
                item {
                    FollowUpQuestionsSection(questions = report.followUpQuestions)
                }
            }
        }
    }
}

@Composable
private fun ReportStatusLine(
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
private fun NotReadyCard(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.research_report_not_ready),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onBack) {
                Text(stringResource(R.string.action_back))
            }
        }
    }
}

@Composable
private fun SummarySection(
    summary: String,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.research_report_executive_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun FindingCard(
    finding: ResearchFindingUiModel,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${finding.number}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = finding.title?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.research_finding_default_title, finding.number),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = finding.body,
                style = MaterialTheme.typography.bodyMedium,
            )

            finding.confidence?.let { confidence ->
                HeritageMetaChip(
                    text = stringResource(R.string.research_finding_confidence_format, confidence * 100),
                )
            }

            if (finding.evidence.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.research_finding_evidence),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    finding.evidence.take(3).forEach { evidence ->
                        Text(
                            text = stringResource(
                                R.string.research_evidence_item_format,
                                evidence.title ?: evidence.evidenceId,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (finding.evidence.size > 3) {
                        Text(
                            text = stringResource(R.string.and_more_format, finding.evidence.size - 3),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LimitationsSection(
    limitations: List<String>,
    warnings: List<String>,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.research_report_limitations),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (limitations.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    limitations.forEach { limitation ->
                        Text(
                            text = "• $limitation",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            if (warnings.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.research_report_warnings),
                        style = MaterialTheme.typography.titleSmall,
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
    }
}

@Composable
private fun FollowUpQuestionsSection(
    questions: List<String>,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.research_report_follow_up_questions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            questions.forEach { question ->
                Text(
                    text = "• $question",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ReportLoadingPlaceholder(modifier: Modifier = Modifier) {
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
