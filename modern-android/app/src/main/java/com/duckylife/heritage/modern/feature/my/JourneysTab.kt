package com.duckylife.heritage.modern.feature.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.text.journeyWarningResId
import com.duckylife.heritage.modern.ui.text.localizedContentType
import com.duckylife.heritage.modern.ui.text.localizedJourneyStrategy

@Composable
internal fun JourneysTab(
    uiState: JourneysUiState,
    selectedStrategy: JourneyStrategy,
    onStrategySelected: (JourneyStrategy) -> Unit,
    onRetry: () -> Unit,
    onItemClick: (MyPageDestination?) -> Unit,
    onViewRelations: (MyPageDestination) -> Unit,
    onTrailStepClick: (MyPageDestination?) -> Unit,
    onBrowseContent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            JourneyHeader(
                selectedStrategy = selectedStrategy,
                onStrategySelected = onStrategySelected,
            )
        }

        item(key = "signals") {
            JourneySignalLine(uiState = uiState)
        }

        when (uiState) {
            is JourneysUiState.Loading -> {
                items(count = SKELETON_COUNT, key = { "skeleton_$it" }) {
                    JourneyCardSkeleton()
                }
            }

            is JourneysUiState.Error -> {
                item(key = "error") {
                    JourneyErrorCard(
                        message = stringResource(uiState.messageResId),
                        onRetry = onRetry,
                    )
                }
            }

            is JourneysUiState.Empty -> {
                item(key = "empty") {
                    EmptyState(
                        icon = Icons.Outlined.Explore,
                        title = stringResource(R.string.journeys_empty_title),
                        message = stringResource(R.string.journeys_empty_message),
                        action = {
                            TextButton(onClick = onBrowseContent) {
                                Text(stringResource(R.string.action_go_discover))
                            }
                        },
                    )
                }
            }

            is JourneysUiState.Success -> {
                if (uiState.trailSteps?.isNotEmpty() == true) {
                    item(key = "trail") {
                        JourneyTrailSection(
                            steps = uiState.trailSteps,
                            onStepClick = onTrailStepClick,
                        )
                    }
                }

                items(
                    items = uiState.items,
                    key = { it.nodeKey },
                ) { item ->
                    JourneyCard(
                        item = item,
                        imageLoader = imageLoader,
                        onClick = { onItemClick(toMyPageDestination(item.contentType, item.targetId)) },
                        onViewRelations = {
                            toMyPageDestination(item.contentType, item.targetId)?.let { dest ->
                                onViewRelations(
                                    MyPageDestination.GraphExplore(
                                        contentType = item.contentType,
                                        contentId = item.targetId.orEmpty(),
                                    ),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JourneyHeader(
    selectedStrategy: JourneyStrategy,
    onStrategySelected: (JourneyStrategy) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val strategies = remember {
        JourneyStrategy.entries.filter { it != JourneyStrategy.Unknown }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.journeys_section_header),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextField(
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                readOnly = true,
                value = localizedJourneyStrategy(selectedStrategy),
                onValueChange = {},
                label = { Text(stringResource(R.string.journeys_strategy_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                strategies.forEach { strategy ->
                    DropdownMenuItem(
                        text = { Text(localizedJourneyStrategy(strategy)) },
                        onClick = {
                            onStrategySelected(strategy)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneySignalLine(
    uiState: JourneysUiState,
    modifier: Modifier = Modifier,
) {
    val signals = when (uiState) {
        is JourneysUiState.Success -> uiState.signals
        is JourneysUiState.Empty -> uiState.signals
        is JourneysUiState.Error -> uiState.signals
        else -> emptyList()
    }
    val warning = when (uiState) {
        is JourneysUiState.Success -> uiState.signalWarning
        is JourneysUiState.Empty -> uiState.signalWarning
        is JourneysUiState.Error -> uiState.signalWarning
        else -> null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (signals.isNotEmpty()) {
            Text(
                text = stringResource(R.string.journeys_signals_prefix, signals.joinToString("、")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (uiState !is JourneysUiState.Loading) {
            Text(
                text = stringResource(R.string.journeys_signals_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        val warningResId = journeyWarningResId(warning)
        if (warningResId != 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(warningResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun JourneyCard(
    item: JourneyUiItem,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
    onViewRelations: () -> Unit,
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
        ) {
            HeritageListImage(
                imageUrl = item.coverImageUrl,
                imageLoader = imageLoader,
                fallbackText = localizedContentType(item.contentType),
                modifier = Modifier.size(width = 80.dp, height = 64.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeritageMetaChip(text = localizedContentType(item.contentType))
                    if (item.isFavorite) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.action_favorite),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                item.subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (item.reasons.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    item.reasons.forEach { reason ->
                        Text(
                            text = "• $reason",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        if (item.isContentNode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onViewRelations) {
                    Text(stringResource(R.string.journeys_view_relations))
                }
            }
        }
    }
}

@Composable
private fun JourneyCardSkeleton(modifier: Modifier = Modifier) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 64.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.3f)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.7f)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JourneyTrailSection(
    steps: List<JourneyTrailStepUiItem>,
    onStepClick: (MyPageDestination?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.journeys_trail_header),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            steps.forEachIndexed { index, step ->
                val enabled = step.isContentNode && !step.targetId.isNullOrBlank()
                ElevatedSuggestionChip(
                    onClick = { onStepClick(toMyPageDestination(step.contentType, step.targetId)) },
                    enabled = enabled,
                    label = {
                        Text("${index + 1}. ${step.title.orEmpty()}")
                    },
                )
            }
        }
    }
}

@Composable
private fun JourneyErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onRetry) {
                Text(stringResource(R.string.journeys_error_retry))
            }
        }
    }
}

private fun toMyPageDestination(contentType: String?, targetId: String?): MyPageDestination? {
    if (targetId.isNullOrBlank()) return null
    return when (contentType) {
        "article" -> MyPageDestination.Article(
            articleId = targetId,
            sourceId = null,
            sourceUrl = null,
            category = ArticleCategory.News,
        )
        "directoryItem" -> MyPageDestination.Directory(
            itemId = targetId,
            sourceId = null,
            kind = DirectoryItemKind.NationalProject,
        )
        "inheritor" -> MyPageDestination.Inheritor(
            inheritorId = targetId,
            sourceId = null,
        )
        else -> null
    }
}

private const val SKELETON_COUNT = 3
