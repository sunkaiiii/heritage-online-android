package com.duckylife.heritage.modern.feature.learningroutes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSectionUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteStepUiModel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedLearningRouteDifficulty

/**
 * 学习路线详情页入口。
 *
 * @param routeId 路线 ID。
 * @param onBack 返回回调。
 * @param onStepContentClick 点击步骤关联内容时回调，参数为 (targetType, targetId)。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningRouteDetailRoute(
    routeId: String,
    onBack: () -> Unit,
    onStepContentClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    val viewModel: LearningRouteDetailViewModel = hiltViewModel<LearningRouteDetailViewModel, LearningRouteDetailViewModel.Factory>(
        key = "learning-route-detail-$routeId",
        creationCallback = { it.create(routeId = routeId) },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val messageText = uiState.snackbarMessage?.let { formatDetailMessage(it) }
    LaunchedEffect(messageText) {
        if (messageText != null) {
            snackbarHostState.showSnackbar(
                message = messageText,
                withDismissAction = true,
            )
            viewModel.consumeSnackbarMessage()
        }
    }

    LearningRouteDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onStepChecked = viewModel::onStepChecked,
        onStepContentClick = onStepContentClick,
        onLoadNextStep = viewModel::loadNextStep,
        onShowRestartConfirmation = viewModel::showRestartConfirmation,
        onDismissRestartConfirmation = viewModel::dismissRestartConfirmation,
        onConfirmRestart = viewModel::confirmRestart,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LearningRouteDetailScreen(
    uiState: LearningRouteDetailUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onStepChecked: (String, Boolean) -> Unit,
    onStepContentClick: (String, String) -> Unit,
    onLoadNextStep: () -> Unit,
    onShowRestartConfirmation: () -> Unit,
    onDismissRestartConfirmation: () -> Unit,
    onConfirmRestart: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.learning_route_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.action_more_options),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_refresh)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                expanded = false
                                onRefresh()
                            },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (uiState.route != null && !uiState.isLoading) {
                LearningRouteDetailBottomBar(
                    isCompleted = uiState.isCompleted,
                    isLoadingNext = uiState.isLoadingNext,
                    nextStepError = uiState.nextStepError,
                    onLoadNextStep = onLoadNextStep,
                    onRestart = onShowRestartConfirmation,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        HeritagePageBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading && uiState.route == null -> LearningRouteDetailLoadingContent()
                uiState.errorKind != null && uiState.route == null -> LearningRouteDetailErrorContent(
                    errorKind = uiState.errorKind,
                    onRetry = onRefresh,
                )
                uiState.route != null -> LearningRouteDetailLoadedContent(
                    route = uiState.route,
                    completedStepIds = uiState.completedStepIds,
                    percent = uiState.percent,
                    completedCount = uiState.completedCount,
                    totalSteps = uiState.totalSteps,
                    nextStep = uiState.nextStep,
                    onStepChecked = onStepChecked,
                    onStepContentClick = onStepContentClick,
                    onLoadNextStep = onLoadNextStep,
                )
            }
        }
    }

    if (uiState.showRestartConfirmDialog) {
        AlertDialog(
            onDismissRequest = onDismissRestartConfirmation,
            title = { Text(stringResource(R.string.learning_route_restart_confirm_title)) },
            text = { Text(stringResource(R.string.learning_route_restart_confirm_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmRestart) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRestartConfirmation) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun LearningRouteDetailLoadedContent(
    route: LearningRouteDetailUiModel,
    completedStepIds: Set<String>,
    percent: Int,
    completedCount: Int,
    totalSteps: Int,
    nextStep: LearningRouteStepUiModel?,
    onStepChecked: (String, Boolean) -> Unit,
    onStepContentClick: (String, String) -> Unit,
    onLoadNextStep: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stepsBySection = remember(route.sections, route.steps) {
        route.sections.associate { section ->
            section to route.steps.filter { it.stepId in section.stepIds }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            LearningRouteHeader(
                route = route,
                percent = percent,
                completedCount = completedCount,
                totalSteps = totalSteps,
            )
        }

        if (route.sections.isNotEmpty()) {
            route.sections.forEach { section ->
                item(key = "section-header-${section.sectionId}") {
                    LearningRouteSectionHeader(
                        section = section,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }

                items(
                    items = stepsBySection[section].orEmpty(),
                    key = { it.stepId },
                ) { step ->
                    LearningRouteStepCard(
                        step = step,
                        checked = step.stepId in completedStepIds,
                        onCheckedChange = { checked -> onStepChecked(step.stepId, checked) },
                        onContentClick = {
                            val type = step.targetType
                            val id = step.targetId
                            if (!type.isNullOrBlank() && !id.isNullOrBlank()) {
                                onStepContentClick(type, id)
                            }
                        },
                    )
                }

                item(key = "section-divider-${section.sectionId}") {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        } else {
            // 无章节时直接列出所有步骤
            items(
                items = route.steps,
                key = { it.stepId },
            ) { step ->
                LearningRouteStepCard(
                    step = step,
                    checked = step.stepId in completedStepIds,
                    onCheckedChange = { checked -> onStepChecked(step.stepId, checked) },
                    onContentClick = {
                        val type = step.targetType
                        val id = step.targetId
                        if (!type.isNullOrBlank() && !id.isNullOrBlank()) {
                            onStepContentClick(type, id)
                        }
                    },
                )
            }
        }

        if (nextStep != null) {
            item {
                NextStepCard(
                    step = nextStep,
                    onClick = {
                        val type = nextStep.targetType
                        val id = nextStep.targetId
                        if (!type.isNullOrBlank() && !id.isNullOrBlank()) {
                            onStepContentClick(type, id)
                        }
                    },
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun LearningRouteHeader(
    route: LearningRouteDetailUiModel,
    percent: Int,
    completedCount: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = route.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeritageMetaChip(text = localizedLearningRouteDifficulty(route.difficulty))
            if (route.estimatedMinutes > 0) {
                Text(
                    text = stringResource(R.string.learning_routes_minutes_format, route.estimatedMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(R.string.learning_routes_steps_format, totalSteps),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (!route.description.isNullOrBlank()) {
            Text(
                text = route.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
        )

        Text(
            text = stringResource(R.string.learning_route_progress_format, completedCount, totalSteps),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LearningRouteSectionHeader(
    section: LearningRouteSectionUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (!section.description.isNullOrBlank()) {
            Text(
                text = section.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LearningRouteStepCard(
    step: LearningRouteStepUiModel,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onContentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = null,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (step.required) {
                        HeritageMetaChip(text = stringResource(R.string.learning_route_step_required))
                    }
                }

                if (!step.description.isNullOrBlank()) {
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (step.estimatedMinutes > 0) {
                    Text(
                        text = stringResource(R.string.learning_routes_minutes_format, step.estimatedMinutes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (!step.targetType.isNullOrBlank() && !step.targetId.isNullOrBlank()) {
                IconButton(onClick = onContentClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = stringResource(R.string.action_open_detail),
                    )
                }
            }
        }
    }
}

@Composable
private fun NextStepCard(
    step: LearningRouteStepUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.learning_route_next_step_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                if (!step.description.isNullOrBlank()) {
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LearningRouteDetailBottomBar(
    isCompleted: Boolean,
    isLoadingNext: Boolean,
    nextStepError: ErrorKind?,
    onLoadNextStep: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (nextStepError != null) {
            Text(
                text = stringResource(nextStepError.fallbackResId()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (isCompleted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.learning_route_completed_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                TextButton(onClick = onRestart) {
                    Text(stringResource(R.string.learning_route_restart))
                }
            }
        } else {
            FilledTonalButton(
                onClick = onLoadNextStep,
                enabled = !isLoadingNext,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoadingNext) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(stringResource(R.string.learning_route_continue_next))
            }
        }
    }
}

@Composable
private fun LearningRouteDetailLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            androidx.compose.material3.CircularProgressIndicator()
            Text(
                text = stringResource(R.string.learning_route_detail_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LearningRouteDetailErrorContent(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.School,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = if (errorKind == ErrorKind.NotFound) {
                    stringResource(R.string.learning_route_not_found)
                } else {
                    stringResource(errorKind.fallbackResId())
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            FilledTonalButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun formatDetailMessage(message: LearningRouteDetailMessage): String {
    return if (message.args.isEmpty()) {
        stringResource(message.resId)
    } else {
        stringResource(message.resId, *message.args.toTypedArray())
    }
}
