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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.feature.my.EmptyState
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportItemUiModel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageEmptyState
import com.duckylife.heritage.modern.ui.component.HeritageErrorState
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.state.AsyncState

@Composable
fun ResearchLibraryRoute(
    onBack: () -> Unit,
    onPackageClick: (String) -> Unit,
    onReportClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResearchLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ResearchLibraryScreen(
        uiState = uiState,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onTabSelected = viewModel::selectTab,
        onPackageClick = onPackageClick,
        onReportClick = onReportClick,
        modifier = modifier,
    )
}

@Composable
internal fun ResearchLibraryScreen(
    uiState: ResearchLibraryUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onTabSelected: (ResearchLibraryTab) -> Unit,
    onPackageClick: (String) -> Unit,
    onReportClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.research_header_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = stringResource(R.string.action_refresh),
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
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                    ResearchLibraryTab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { onTabSelected(tab) },
                            text = {
                                Text(
                                    stringResource(
                                        when (tab) {
                                            ResearchLibraryTab.Packages -> R.string.research_packages_tab
                                            ResearchLibraryTab.Reports -> R.string.research_reports_tab
                                        },
                                    ),
                                )
                            },
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                when (uiState.selectedTab) {
                    ResearchLibraryTab.Packages -> PackagesList(
                        state = uiState.packages,
                        onRetry = onRefresh,
                        onPackageClick = onPackageClick,
                        modifier = Modifier.fillMaxSize(),
                    )

                    ResearchLibraryTab.Reports -> ReportsList(
                        state = uiState.reports,
                        onRetry = onRefresh,
                        onReportClick = onReportClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun PackagesList(
    state: AsyncState<List<ResearchPackageItemUiModel>>,
    onRetry: () -> Unit,
    onPackageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading && state.data == null -> LoadingPlaceholder(modifier)
        state.errorKind != null && state.data == null -> HeritageErrorState(
            errorKind = state.errorKind,
            onRetry = onRetry,
            modifier = modifier.padding(20.dp),
        )

        state.data.isNullOrEmpty() -> EmptyState(
            icon = Icons.Outlined.Folder,
            title = stringResource(R.string.research_empty_title),
            message = stringResource(R.string.research_empty_message),
            modifier = modifier.fillMaxWidth(),
        )

        else -> {
            val packages = state.data
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(packages, key = { it.packageId }) { item ->
                    PackageCard(
                        item = item,
                        onClick = { onPackageClick(item.packageId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PackageCard(
    item: ResearchPackageItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isClickable = item.isClickable && item.packageId.isNotBlank()
    HeritageContentCard(
        onClick = if (isClickable) onClick else null,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(status = item.status)
            }

            if (!item.subtitle.isNullOrBlank()) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HeritageMetaChip(
                    text = stringResource(R.string.research_package_artifact_count_format, item.artifactCount),
                )
                if (item.includesContent) {
                    HeritageMetaChip(text = stringResource(R.string.research_package_includes_content))
                }
                if (item.includesEvidence) {
                    HeritageMetaChip(text = stringResource(R.string.research_package_includes_evidence))
                }
            }

            if (!isClickable) {
                Text(
                    text = stringResource(
                        when (item.status) {
                            ResearchTaskStatus.Running -> R.string.research_package_running_message
                            ResearchTaskStatus.Failed -> R.string.research_package_failed_message
                            ResearchTaskStatus.Queued -> R.string.research_package_queued_message
                            else -> R.string.research_package_no_report
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ReportsList(
    state: AsyncState<List<ResearchReportItemUiModel>>,
    onRetry: () -> Unit,
    onReportClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading && state.data == null -> LoadingPlaceholder(modifier)
        state.errorKind != null && state.data == null -> HeritageErrorState(
            errorKind = state.errorKind,
            onRetry = onRetry,
            modifier = modifier.padding(20.dp),
        )

        state.data.isNullOrEmpty() -> HeritageEmptyState(
            message = stringResource(R.string.research_empty_message),
            modifier = modifier.padding(20.dp),
        )

        else -> {
            val reports = state.data
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(reports, key = { it.reportId }) { item ->
                    ReportCard(
                        item = item,
                        onClick = { onReportClick(item.reportId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    item: ResearchReportItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isClickable = item.reportId.isNotBlank()
    HeritageContentCard(
        onClick = if (isClickable) onClick else null,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(status = item.status)
            }

            if (!item.modelName.isNullOrBlank()) {
                Text(
                    text = item.modelName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: ResearchTaskStatus,
    modifier: Modifier = Modifier,
) {
    val (containerColor, contentColor) = when (status) {
        ResearchTaskStatus.Succeeded ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ResearchTaskStatus.Running, ResearchTaskStatus.Queued ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        ResearchTaskStatus.Failed, ResearchTaskStatus.Cancelled ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        ResearchTaskStatus.Unknown ->
            MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
    }
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        contentColor = contentColor,
    ) {
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
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier = Modifier) {
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
