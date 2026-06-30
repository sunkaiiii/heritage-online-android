package com.duckylife.heritage.modern.feature.rankings

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingFilters
import com.duckylife.heritage.modern.feature.rankings.model.RankingItemUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingMetricUiModel
import com.duckylife.heritage.modern.ui.state.AsyncState
import com.duckylife.heritage.modern.ui.component.HeritageEmptyState
import com.duckylife.heritage.modern.ui.component.HeritageErrorState
import com.duckylife.heritage.modern.feature.detail.DetailContextTarget
import com.duckylife.heritage.modern.feature.detail.toDetailContextTarget
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

private const val RANKING_REASONS_MAX_COUNT = 5
private const val RANKING_METRICS_MAX_COUNT = 10

@Composable
fun RankingsRoute(
    onBack: () -> Unit,
    onRankingClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RankingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var contentMetric by rememberSaveable { mutableStateOf<String?>(null) }

    if (contentMetric != null) {
        val metric = contentMetric.orEmpty()
        RankingContentBottomSheet(
            metric = metric,
            content = uiState.content,
            onDismiss = {
                contentMetric = null
                viewModel.clearRankingContent()
            },
            onRetry = {
                viewModel.loadRankingContent(
                    metric = RankingMetric.entries.firstOrNull { it.wireName == metric }
                        ?: RankingMetric.Unknown,
                    filters = RankingFilters(),
                )
            },
            onContentClick = {},
        )
    }

    RankingsScreen(
        uiState = uiState,
        onBack = onBack,
        onRefresh = viewModel::loadRankings,
        onRankingClick = onRankingClick,
        onViewContent = { metric ->
            contentMetric = metric.wireName
            viewModel.loadRankingContent(metric, RankingFilters())
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RankingsScreen(
    uiState: RankingsUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onRankingClick: (String) -> Unit,
    onViewContent: (RankingMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.rankings_title))
                        Text(
                            text = stringResource(R.string.rankings_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        HeritagePageBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                uiState.definitions.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.definitions.errorKind != null -> HeritageErrorState(
                    errorKind = uiState.definitions.errorKind,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                )

                uiState.definitions.data != null -> {
                    val definitions = uiState.definitions.data
                    if (definitions.isEmpty()) {
                        HeritageEmptyState(
                            message = stringResource(R.string.ranking_empty_title),
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(definitions, key = { it.rankingId }) { definition ->
                                RankingDefinitionCard(
                                    definition = definition,
                                    onClick = { onRankingClick(definition.rankingId) },
                                    onViewContent = onViewContent,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingDefinitionCard(
    definition: RankingDefinitionUiModel,
    onClick: () -> Unit,
    onViewContent: (RankingMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isClickable = definition.rankingId.isNotBlank()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = definition.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            definition.description?.let { description ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            definition.refreshHint?.let { hint ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                )
            }
            definition.metric
                ?.takeIf { it.isNotBlank() && it != RankingMetric.Unknown.wireName }
                ?.let { metricString ->
                    val metric = RankingMetric.entries.firstOrNull { it.wireName == metricString }
                        ?: RankingMetric.Unknown
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { onViewContent(metric) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(stringResource(R.string.ranking_view_content))
                    }
                }
        }
    }
}

@Composable
fun RankingDetailRoute(
    rankingId: String,
    onBack: () -> Unit,
    onContentClick: (DetailContextTarget) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RankingDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(rankingId) {
        viewModel.setRankingId(rankingId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RankingDetailScreen(
        rankingId = rankingId,
        uiState = uiState,
        onBack = onBack,
        onRefresh = viewModel::loadDetail,
        onFiltersChanged = viewModel::updateFilters,
        onContentClick = onContentClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RankingDetailScreen(
    rankingId: String,
    uiState: RankingDetailUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onFiltersChanged: (RankingFilters) -> Unit,
    onContentClick: (DetailContextTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.detail.data?.title ?: stringResource(R.string.ranking_detail_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = stringResource(R.string.ranking_filter_title),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        HeritagePageBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                uiState.detail.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.detail.errorKind != null -> HeritageErrorState(
                    errorKind = uiState.detail.errorKind,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                )

                uiState.detail.data != null -> {
                    val detail = uiState.detail.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        detail.description?.let { description ->
                            item {
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }
                        if (detail.items.isEmpty()) {
                            item {
                                HeritageEmptyState(
                                    message = stringResource(R.string.ranking_empty_title),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        } else {
                            itemsIndexed(detail.items, key = { index, item -> "${index}-${item.rank}-${item.targetId ?: item.title}" }) { _, item ->
                                RankingItemCard(
                                    item = item,
                                    onClick = {
                                        item.toDetailContextTarget()?.let(onContentClick)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        RankingFilterSheet(
            filters = uiState.filters,
            onFiltersChanged = {
                onFiltersChanged(it)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false },
        )
    }
}

@Composable
private fun RankingItemCard(
    item: RankingItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val isClickable = !item.contentType.isNullOrBlank() && !item.contentId.isNullOrBlank()
    val rankColor = when (item.rank) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (item.rank <= 3) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = stringResource(
                                when (item.rank) {
                                    1 -> R.string.ranking_top_1_content_description
                                    2 -> R.string.ranking_top_2_content_description
                                    else -> R.string.ranking_top_3_content_description
                                },
                            ),
                            tint = rankColor,
                            modifier = Modifier.size(32.dp),
                        )
                    } else {
                        Text(
                            text = item.rank.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = rankColor,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    item.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "%.1f".format(item.score),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.ranking_score_label),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            if (item.metrics.isNotEmpty() || item.reasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        text = stringResource(
                            if (expanded) R.string.ranking_metrics_collapse else R.string.ranking_metrics_expand,
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = null,
                    )
                }
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (item.metrics.isNotEmpty()) {
                        RankingMetricsList(metrics = item.metrics)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (item.reasons.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.ranking_reasons_title),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        item.reasons.take(RANKING_REASONS_MAX_COUNT).forEach { reason ->
                            Text(
                                text = "• $reason",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (item.reasons.size > RANKING_REASONS_MAX_COUNT) {
                            Text(
                                text = stringResource(
                                    R.string.and_more_format,
                                    item.reasons.size - RANKING_REASONS_MAX_COUNT,
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingMetricsList(
    metrics: List<RankingMetricUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        metrics.take(RANKING_METRICS_MAX_COUNT).forEach { metric ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = metric.label ?: metric.key,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "%.2f".format(metric.value),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (metrics.size > RANKING_METRICS_MAX_COUNT) {
            Text(
                text = stringResource(
                    R.string.and_more_format,
                    metrics.size - RANKING_METRICS_MAX_COUNT,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankingContentBottomSheet(
    metric: String,
    content: AsyncState<RankingDetailUiModel>,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onContentClick: (DetailContextTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.ranking_view_content),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = metric,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            when {
                content.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                content.errorKind != null -> HeritageErrorState(
                    errorKind = content.errorKind,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )

                content.data != null -> {
                    val detail = content.data
                    if (detail.items.isEmpty()) {
                        HeritageEmptyState(
                            message = stringResource(R.string.ranking_empty_title),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            detail.items.forEach { item ->
                                RankingItemCard(
                                    item = item,
                                    onClick = {
                                        item.toDetailContextTarget()?.let(onContentClick)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Rankings", showBackground = true)
@Composable
private fun RankingsScreenPreview() {
    HeritageTheme {
        RankingsScreen(
            uiState = RankingsUiState(
                definitions = AsyncState(
                    data = listOf(
                        RankingDefinitionUiModel(
                            rankingId = "most-viewed",
                            title = "最多浏览",
                            description = "近期被浏览次数最多的内容。",
                            metric = "total",
                            refreshHint = "每日更新",
                        ),
                        RankingDefinitionUiModel(
                            rankingId = "hidden-gems",
                            title = "冷门遗珠",
                            description = "质量高但曝光较少的内容。",
                            metric = "hiddenGem",
                        ),
                        RankingDefinitionUiModel(
                            rankingId = "richest-media",
                            title = "图片最丰富",
                            description = "拥有最多高清图片的内容。",
                            metric = "imageRichness",
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRefresh = {},
            onRankingClick = {},
            onViewContent = {},
        )
    }
}

@Preview(name = "Rankings dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RankingsScreenDarkPreview() {
    RankingsScreenPreview()
}

@Preview(name = "Ranking detail", showBackground = true)
@Composable
private fun RankingDetailScreenPreview() {
    HeritageTheme {
        RankingDetailScreen(
            rankingId = "most-viewed",
            uiState = RankingDetailUiState(
                rankingId = "most-viewed",
                detail = AsyncState(
                    data = RankingDetailUiModel(
                        rankingId = "most-viewed",
                        title = "最多浏览",
                        description = "近期被浏览次数最多的内容排行。",
                        metric = "total",
                        items = listOf(
                            RankingItemUiModel(
                                rank = 1,
                                targetType = ContentTargetType.Article,
                                targetId = "art-1",
                                title = "中国剪纸",
                                subtitle = "传统美术 · 陕西",
                                score = 9876.5,
                                metrics = listOf(
                                    RankingMetricUiModel(key = "views", label = "浏览量", value = 9876.0),
                                    RankingMetricUiModel(key = "shares", label = "分享数", value = 432.0),
                                ),
                                reasons = listOf("春节期间搜索量激增", "被列入多份学习路线"),
                                contentId = "art-1",
                                contentType = "article",
                            ),
                            RankingItemUiModel(
                                rank = 2,
                                targetType = ContentTargetType.DirectoryItem,
                                targetId = "dir-1",
                                title = "京剧",
                                subtitle = "传统戏剧 · 北京",
                                score = 8654.3,
                                metrics = listOf(
                                    RankingMetricUiModel(key = "views", label = "浏览量", value = 8654.0),
                                ),
                                reasons = listOf("代表性项目关注度高"),
                                contentId = "dir-1",
                                contentType = "directoryItem",
                            ),
                            RankingItemUiModel(
                                rank = 3,
                                targetType = ContentTargetType.Inheritor,
                                targetId = "inh-1",
                                title = "梅兰芳",
                                subtitle = "传统戏剧 · 北京",
                                score = 7432.1,
                                metrics = listOf(
                                    RankingMetricUiModel(key = "views", label = "浏览量", value = 7432.0),
                                ),
                                reasons = emptyList(),
                                contentId = "inh-1",
                                contentType = "inheritor",
                            ),
                            RankingItemUiModel(
                                rank = 4,
                                targetType = ContentTargetType.Article,
                                targetId = "art-2",
                                title = "皮影戏",
                                subtitle = "传统戏剧 · 河北",
                                score = 6543.0,
                                metrics = emptyList(),
                                reasons = emptyList(),
                                contentId = "art-2",
                                contentType = "article",
                            ),
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRefresh = {},
            onFiltersChanged = {},
            onContentClick = {},
        )
    }
}

@Preview(name = "Ranking detail dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RankingDetailScreenDarkPreview() {
    RankingDetailScreenPreview()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankingFilterSheet(
    filters: RankingFilters,
    onFiltersChanged: (RankingFilters) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var localFilters by remember { mutableStateOf(filters) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.ranking_filter_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            val targetTypes = listOf(
                ContentTargetType.All to R.string.ranking_target_type_all,
                ContentTargetType.Article to R.string.ranking_target_type_article,
                ContentTargetType.DirectoryItem to R.string.ranking_target_type_directoryItem,
                ContentTargetType.Inheritor to R.string.ranking_target_type_inheritor,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                targetTypes.forEach { (value, labelRes) ->
                    FilterChip(
                        selected = localFilters.targetType == value,
                        onClick = { localFilters = localFilters.copy(targetType = value.takeIf { it != ContentTargetType.All }) },
                        label = { Text(stringResource(labelRes)) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = localFilters.region ?: "",
                onValueChange = { localFilters = localFilters.copy(region = it.takeIf { it.isNotBlank() }) },
                label = { Text(stringResource(R.string.spacetime_filter_region)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = localFilters.category ?: "",
                onValueChange = { localFilters = localFilters.copy(category = it.takeIf { it.isNotBlank() }) },
                label = { Text(stringResource(R.string.spacetime_filter_category)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = localFilters.year?.toString() ?: "",
                onValueChange = { localFilters = localFilters.copy(year = it.toIntOrNull()) },
                label = { Text(stringResource(R.string.filter_field_year)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = { localFilters = RankingFilters() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.filter_clear))
                }
                Button(
                    onClick = { onFiltersChanged(localFilters) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.filter_apply))
                }
            }
        }
    }
}
