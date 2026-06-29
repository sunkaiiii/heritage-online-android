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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedLearningRouteDifficulty

@Composable
fun LearningRoutesRoute(
    seedType: String?,
    seedId: String?,
    onBack: () -> Unit,
    onRouteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LearningRoutesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(seedType, seedId) {
        viewModel.setSeed(seedType, seedId)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { routeId ->
            onRouteClick(routeId)
        }
    }

    val buildError = uiState.buildSeedError
    LaunchedEffect(buildError) {
        if (buildError != null) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.learning_routes_build_failed),
                withDismissAction = true,
            )
            viewModel.clearBuildError()
        }
    }

    LearningRoutesScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onDifficultySelected = viewModel::selectDifficulty,
        onRouteClick = onRouteClick,
        onBuildFromSeed = viewModel::buildFromSeed,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LearningRoutesScreen(
    uiState: LearningRoutesUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onDifficultySelected: (LearningRouteDifficulty) -> Unit,
    onRouteClick: (String) -> Unit,
    onBuildFromSeed: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.learning_routes_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        HeritagePageBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        DifficultyFilterChips(
                            selectedDifficulty = uiState.selectedDifficulty,
                            onDifficultySelected = onDifficultySelected,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    if (uiState.seedType != null && uiState.seedId != null) {
                        item {
                            BuildFromSeedCard(
                                isBuilding = uiState.isBuildingSeed,
                                onClick = onBuildFromSeed,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }

                    when {
                        uiState.routes.isLoading && !uiState.routes.hasData -> {
                            items(4) {
                                RouteCardSkeleton(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }

                        uiState.routes.hasFatalError -> {
                            item {
                                LearningRoutesErrorContent(
                                    errorKind = uiState.routes.errorKind ?: ErrorKind.Unknown,
                                    onRetry = onRetry,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }

                        uiState.routes.hasData -> {
                            val routes = uiState.routes.data.orEmpty()
                            if (routes.isEmpty()) {
                                item {
                                    LearningRoutesEmptyContent(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                            } else {
                                items(routes, key = { it.routeId }) { route ->
                                    LearningRouteCard(
                                        route = route,
                                        onClick = { onRouteClick(route.routeId) },
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyFilterChips(
    selectedDifficulty: LearningRouteDifficulty,
    onDifficultySelected: (LearningRouteDifficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    val difficulties = listOf(
        LearningRouteDifficulty.All,
        LearningRouteDifficulty.Beginner,
        LearningRouteDifficulty.Intermediate,
        LearningRouteDifficulty.Deep,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        difficulties.forEach { difficulty ->
            FilterChip(
                selected = selectedDifficulty == difficulty,
                onClick = { onDifficultySelected(difficulty) },
                label = { Text(text = localizedLearningRouteDifficulty(difficulty)) },
            )
        }
    }
}

@Composable
private fun BuildFromSeedCard(
    isBuilding: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = if (isBuilding) null else onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.School,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.learning_routes_build_from_seed_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.learning_routes_build_from_seed_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isBuilding) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                TextButton(onClick = onClick) {
                    Text(stringResource(R.string.learning_routes_build_action))
                }
            }
        }
    }
}

@Composable
private fun LearningRouteCard(
    route: LearningRouteSummaryUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = rememberHeritageImageLoader(),
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            HeritageListImage(
                imageUrl = route.coverImageUrl,
                imageLoader = imageLoader,
                contentDescription = route.title,
                fallbackText = route.title.take(1),
                modifier = Modifier.size(72.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = route.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!route.subtitle.isNullOrBlank()) {
                    Text(
                        text = route.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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
                        text = stringResource(R.string.learning_routes_steps_format, route.stepCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (route.tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        route.tags.take(2).forEach { tag ->
                            HeritageMetaChip(text = tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteCardSkeleton(
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerPlaceholder(),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .shimmerPlaceholder(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .shimmerPlaceholder(),
                )
            }
        }
    }
}

@Composable
private fun LearningRoutesEmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.School,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.learning_routes_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.learning_routes_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun LearningRoutesErrorContent(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(errorKind.fallbackResId()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun Modifier.shimmerPlaceholder(): Modifier =
    this.background(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = MaterialTheme.shapes.small,
    )
