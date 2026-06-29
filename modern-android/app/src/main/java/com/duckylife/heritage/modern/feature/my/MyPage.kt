@file:OptIn(ExperimentalMaterial3Api::class)

package com.duckylife.heritage.modern.feature.my

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.extractCoverImageUrl
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.feature.research.ResearchLibraryRoute
import com.duckylife.heritage.modern.feature.research.ResearchPackageRoute
import com.duckylife.heritage.modern.feature.research.ResearchReportRoute
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import androidx.compose.ui.tooling.preview.Preview
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.launch

sealed interface MyPageDestination {
    data class Article(
        val articleId: String?,
        val sourceId: String?,
        val sourceUrl: String?,
        val category: com.duckylife.heritage.modern.core.network.dto.ArticleCategory,
    ) : MyPageDestination

    data class Directory(
        val itemId: String?,
        val sourceId: String?,
        val kind: com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind,
    ) : MyPageDestination

    data class Inheritor(
        val inheritorId: String?,
        val sourceId: String?,
    ) : MyPageDestination

    data class GraphExplore(
        val contentType: String,
        val contentId: String,
        val initialTabName: String = "similar",
    ) : MyPageDestination

    data class LearningRoutes(
        val seedType: String? = null,
        val seedId: String? = null,
    ) : MyPageDestination

    data class LearningRouteDetail(
        val routeId: String,
    ) : MyPageDestination
}

private enum class MyPageTab(
    val titleRes: Int,
) {
    Favorites(R.string.favorites_tab),
    Browsing(R.string.browsing_tab),
    Learning(R.string.learning_tab),
    Journeys(R.string.journeys_tab),
    Research(R.string.research_tab),
}

@Composable
fun MyPage(
    onBack: () -> Unit,
    onNavigate: (MyPageDestination) -> Unit,
    onNavigateToDiscovery: () -> Unit = {},
    onNavigateToLearningRoutes: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
    readingPathViewModel: ReadingPathViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val readingPathEvents by readingPathViewModel.events.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingOperationCount.collectAsStateWithLifecycle()
    val learningProgress by viewModel.learningProgress.collectAsStateWithLifecycle()
    val journeys by viewModel.journeys.collectAsStateWithLifecycle()
    val selectedStrategy by viewModel.selectedStrategy.collectAsStateWithLifecycle()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedResearchPackageId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedResearchReportId by rememberSaveable { mutableStateOf<String?>(null) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearReadingPathDialog by remember { mutableStateOf(false) }
    var showSyncSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val imageLoader = rememberHeritageImageLoader()
    val favoriteRemovedMessage = stringResource(R.string.favorite_removed_message)

    Box(modifier = modifier.fillMaxSize()) {
        HeritagePageBackground(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                MyPageTopBar(
                    onBack = onBack,
                    pendingCount = pendingCount,
                    lastSyncError = profileState?.lastSyncError,
                    onSyncStatusClick = { showSyncSheet = true },
                )

                ProfileOverviewRow(
                    favoriteCount = profileState?.favoriteCount ?: favorites.size.toLong(),
                    historyCount = profileState?.historyCount ?: history.size.toLong(),
                    learningRouteCount = profileState?.learningRouteCount ?: learningProgress.size.toLong(),
                    pendingCount = pendingCount,
                    lastSyncError = profileState?.lastSyncError,
                    onRefresh = { viewModel.syncNow() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )

                SecondaryScrollableTabRow(selectedTabIndex = selectedTabIndex, edgePadding = 20.dp) {
                    MyPageTab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(stringResource(tab.titleRes)) },
                        )
                    }
                }

                when (MyPageTab.entries[selectedTabIndex]) {
                    MyPageTab.Favorites -> FavoritesTab(
                        favorites = favorites,
                        pendingCount = pendingCount,
                        lastSyncAt = profileState?.lastSyncAt,
                        imageLoader = imageLoader,
                        onItemClick = { item ->
                            item.navigationTarget?.let { onNavigate(it) }
                        },
                        onUnfavorite = { item ->
                            scope.launch {
                                snackbarHostState.showSnackbar(favoriteRemovedMessage)
                            }
                            viewModel.unfavorite(item)
                        },
                        onBrowseContent = onNavigateToDiscovery,
                    )

                    MyPageTab.Browsing -> BrowsingTab(
                        history = history,
                        readingPathEvents = readingPathEvents,
                        lastSyncError = profileState?.lastSyncError,
                        onItemClick = { item ->
                            item.navigationTarget?.let { onNavigate(it) }
                        },
                        onReadingPathItemClick = { event ->
                            event.toMyPageDestination()?.let { onNavigate(it) }
                        },
                        onClearHistory = { showClearHistoryDialog = true },
                        onClearReadingPath = { showClearReadingPathDialog = true },
                    )

                    MyPageTab.Learning -> LearningTab(
                        progress = learningProgress.map { it.toLearningItem() },
                        onRouteSelected = { routeId ->
                            onNavigate(MyPageDestination.LearningRouteDetail(routeId = routeId))
                        },
                        onBrowseRoutes = onNavigateToLearningRoutes,
                    )

                    MyPageTab.Journeys -> JourneysTab(
                        uiState = journeys,
                        selectedStrategy = selectedStrategy,
                        onStrategySelected = { viewModel.selectStrategy(it) },
                        onRetry = { viewModel.retryJourneys() },
                        onItemClick = { destination -> destination?.let { onNavigate(it) } },
                        onViewRelations = { destination -> onNavigate(destination) },
                        onTrailStepClick = { destination -> destination?.let { onNavigate(it) } },
                        onBrowseContent = onNavigateToDiscovery,
                        modifier = Modifier.fillMaxSize(),
                    )

                    MyPageTab.Research -> ResearchLibraryRoute(
                        onBack = onBack,
                        onPackageClick = { packageId ->
                            selectedResearchReportId = null
                            selectedResearchPackageId = packageId
                        },
                        onReportClick = { reportId ->
                            selectedResearchPackageId = null
                            selectedResearchReportId = reportId
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            selectedResearchPackageId?.let { packageId ->
                ResearchPackageRoute(
                    packageId = packageId,
                    onBack = {
                        selectedResearchPackageId = null
                        selectedResearchReportId = null
                    },
                    onViewReport = { reportId ->
                        selectedResearchPackageId = null
                        selectedResearchReportId = reportId
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (selectedResearchPackageId == null) {
                selectedResearchReportId?.let { reportId ->
                    ResearchReportRoute(
                        reportId = reportId,
                        onBack = {
                            selectedResearchPackageId = null
                            selectedResearchReportId = null
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text(stringResource(R.string.action_clear_history)) },
            text = { Text(stringResource(R.string.action_clear_history_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearHistoryDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showClearReadingPathDialog) {
        AlertDialog(
            onDismissRequest = { showClearReadingPathDialog = false },
            title = { Text(stringResource(R.string.reading_path_clear)) },
            text = { Text(stringResource(R.string.reading_path_clear_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    readingPathViewModel.clearAll()
                    showClearReadingPathDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearReadingPathDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showSyncSheet) {
        SyncStatusSheet(
            pendingCount = pendingCount,
            lastSyncAt = profileState?.lastSyncAt,
            lastSyncError = profileState?.lastSyncError,
            onDismiss = { showSyncSheet = false },
            onRefresh = { viewModel.syncNow() },
        )
    }
}

@Composable
private fun MyPageTopBar(
    onBack: () -> Unit,
    pendingCount: Int,
    lastSyncError: String?,
    onSyncStatusClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
            )
        }
        Text(
            text = stringResource(R.string.my_space_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        SyncStatusIcon(
            pendingCount = pendingCount,
            lastSyncError = lastSyncError,
            onClick = onSyncStatusClick,
        )
    }
}

@Composable
private fun SyncStatusIcon(
    pendingCount: Int,
    lastSyncError: String?,
    onClick: () -> Unit,
) {
    when {
        lastSyncError != null -> {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = stringResource(R.string.sync_status_error),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }

        pendingCount > 0 -> {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
        }

        else -> {
            // 成功状态不占额外空间。
        }
    }
}

@Composable
private fun ProfileOverviewRow(
    favoriteCount: Long,
    historyCount: Long,
    learningRouteCount: Long,
    pendingCount: Int,
    lastSyncError: String?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.my_profile_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(
                    R.string.profile_counts_format,
                    favoriteCount,
                    historyCount,
                    learningRouteCount,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SyncStatusLine(
                pendingCount = pendingCount,
                lastSyncError = lastSyncError,
            )
        }
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = stringResource(R.string.action_sync_now),
            )
        }
    }
}

@Composable
private fun SyncStatusLine(
    pendingCount: Int,
    lastSyncError: String?,
) {
    val text = when {
        lastSyncError != null -> stringResource(R.string.sync_status_error)
        pendingCount > 0 -> stringResource(R.string.sync_status_pending)
        else -> stringResource(R.string.sync_status_success)
    }
    val color = when {
        lastSyncError != null -> MaterialTheme.colorScheme.error
        pendingCount > 0 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
    )
}

@Composable
private fun FavoritesTab(
    favorites: List<FavoriteItem>,
    pendingCount: Int,
    lastSyncAt: Long?,
    imageLoader: ImageLoader,
    onItemClick: (FavoriteItem) -> Unit,
    onUnfavorite: (FavoriteItem) -> Unit,
    onBrowseContent: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (favorites.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = stringResource(R.string.favorites_empty_title),
                    message = stringResource(R.string.favorites_empty_message_detail),
                    action = {
                        TextButton(onClick = onBrowseContent) {
                            Text(stringResource(R.string.action_go_discover))
                        }
                    },
                )
            }
        } else {
            item {
                SectionHeader(
                    title = stringResource(R.string.favorites_section_header),
                    subtitle = when {
                        pendingCount > 0 -> stringResource(R.string.sync_status_pending)
                        lastSyncAt != null -> stringResource(
                            R.string.last_sync_at_format,
                            formatSyncTime(lastSyncAt),
                        )
                        else -> null
                    },
                )
            }
            items(favorites, key = { it.key }) { item ->
                FavoriteCard(
                    item = item,
                    imageLoader = imageLoader,
                    onClick = { onItemClick(item) },
                    onUnfavorite = { onUnfavorite(item) },
                )
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    item: FavoriteItem,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
    onUnfavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fallbackText = stringResource(R.string.brand_fallback)
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeritageListImage(
                imageUrl = item.coverImageUrl,
                imageLoader = imageLoader,
                fallbackText = fallbackText,
                modifier = Modifier
                    .size(width = 72.dp, height = 72.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.title.orEmpty().ifBlank {
                        stringResource(
                            when (item.targetType) {
                                "inheritor" -> R.string.unnamed_inheritor
                                "directoryItem" -> R.string.unnamed_directory_item
                                else -> R.string.unnamed_article
                            },
                        )
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeritageMetaChip(text = localizedTypeLabel(item.targetType))
                    item.tags.take(3).forEach { tag ->
                        if (tag.isNotBlank()) {
                            HeritageMetaChip(text = tag)
                        }
                    }
                }
            }
            IconButton(onClick = onUnfavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.action_unfavorite),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun BrowsingTab(
    history: List<HistoryItem>,
    readingPathEvents: List<ReadingPathEvent>,
    lastSyncError: String?,
    onItemClick: (HistoryItem) -> Unit,
    onReadingPathItemClick: (ReadingPathEvent) -> Unit,
    onClearHistory: () -> Unit,
    onClearReadingPath: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (lastSyncError != null && history.isNotEmpty()) {
            item {
                OfflineNotice()
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.browsing_recent_header),
                action = if (history.isNotEmpty()) {
                    {
                        TextButton(onClick = onClearHistory) {
                            Icon(
                                imageVector = Icons.Outlined.ClearAll,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(stringResource(R.string.action_clear_history))
                        }
                    }
                } else {
                    null
                },
            )
        }

        if (history.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Visibility,
                    title = stringResource(R.string.history_empty_title),
                    message = stringResource(R.string.recent_empty_message),
                )
            }
        } else {
            items(history, key = { it.key }) { item ->
                HistoryRow(
                    item = item,
                    onClick = { onItemClick(item) },
                )
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.reading_path_section_header),
                action = if (readingPathEvents.isNotEmpty()) {
                    {
                        TextButton(onClick = onClearReadingPath) {
                            Icon(
                                imageVector = Icons.Outlined.ClearAll,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(stringResource(R.string.reading_path_clear))
                        }
                    }
                } else {
                    null
                },
            )
        }

        if (readingPathEvents.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Map,
                    title = stringResource(R.string.reading_path_empty),
                    message = stringResource(R.string.reading_path_empty),
                )
            }
        } else {
            items(readingPathEvents, key = { it.id }) { event ->
                ReadingPathRow(
                    event = event,
                    onClick = { onReadingPathItemClick(event) },
                )
            }
        }
    }
}

@Composable
private fun OfflineNotice(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.history_offline_notice),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun HistoryRow(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.title.orEmpty().ifBlank {
                        stringResource(
                            when (item.targetType) {
                                "inheritor" -> R.string.unnamed_inheritor
                                "directoryItem" -> R.string.unnamed_directory_item
                                else -> R.string.unnamed_article
                            },
                        )
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeritageMetaChip(text = localizedTypeLabel(item.targetType))
                    Text(
                        text = stringResource(
                            R.string.history_view_count_format,
                            item.viewCount,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = formatViewedAt(item.viewedAt, item.legacyLastViewedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LearningTab(
    progress: List<LearningProgressItem>,
    onRouteSelected: (String) -> Unit,
    onBrowseRoutes: () -> Unit,
) {
    val ongoing = remember(progress) { progress.filter { it.percent in 0..99 } }
    val completed = remember(progress) { progress.filter { it.percent == 100 } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (progress.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.School,
                    title = stringResource(R.string.learning_empty_title),
                    message = stringResource(R.string.learning_empty_message),
                    action = {
                        TextButton(onClick = onBrowseRoutes) {
                            Text(stringResource(R.string.action_browse_learning_routes))
                        }
                    },
                )
            }
        } else {
            if (ongoing.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.learning_ongoing_header),
                        subtitle = null,
                    )
                }
                items(ongoing, key = { it.routeId }) { item ->
                    LearningProgressRow(
                        item = item,
                        isCompleted = false,
                        onClick = { onRouteSelected(item.routeId) },
                    )
                }
            }

            if (completed.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.learning_completed_header),
                        subtitle = null,
                    )
                }
                items(completed, key = { it.routeId }) { item ->
                    LearningProgressRow(
                        item = item,
                        isCompleted = true,
                        onClick = { onRouteSelected(item.routeId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LearningProgressRow(
    item: LearningProgressItem,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.routeTitle.orEmpty().ifBlank { item.routeId },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isCompleted) {
                Text(
                    text = stringResource(
                        R.string.learning_completed_format,
                        formatIsoDate(item.completedAt),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = stringResource(
                        R.string.learning_progress_format,
                        item.completedStepCount,
                        item.percent,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (item.percent / 100f).coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onClick) {
                    Text(
                        stringResource(
                            if (isCompleted) R.string.learning_route_restart else R.string.learning_route_continue,
                        ),
                    )
                }
            }
        }
    }
}


@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        action?.invoke()
    }
}

@Composable
internal fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        action?.invoke()
    }
}

@Composable
private fun SyncStatusSheet(
    pendingCount: Int,
    lastSyncAt: Long?,
    lastSyncError: String?,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.action_sync_now),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val icon = when {
                    lastSyncError != null -> Icons.Outlined.CloudOff
                    pendingCount > 0 -> Icons.Outlined.CloudDone
                    else -> Icons.Outlined.CloudDone
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (lastSyncError != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
                Text(
                    text = when {
                        lastSyncError != null -> stringResource(R.string.sync_status_error)
                        pendingCount > 0 -> stringResource(R.string.sync_status_pending)
                        else -> stringResource(R.string.sync_status_success)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            lastSyncAt?.let { syncAt ->
                Text(
                    text = stringResource(
                        R.string.last_sync_at_format,
                        formatSyncTime(syncAt),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            lastSyncError?.let { error ->
                val errorKind = ErrorKind.entries.find { it.name == error } ?: ErrorKind.Unknown
                Text(
                    text = stringResource(errorKind.fallbackResId()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            TextButton(
                onClick = {
                    onRefresh()
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.action_sync_now))
            }
        }
    }
}

@Composable
private fun ReadingPathRow(
    event: ReadingPathEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeritageMetaChip(text = localizedTypeLabel(event.toType))
                    if (!event.source.isNullOrBlank()) {
                        HeritageMetaChip(text = localizedSourceLabel(event.source))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.toTitle.orEmpty().ifBlank { event.toId },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun localizedTypeLabel(type: String): String =
    com.duckylife.heritage.modern.ui.text.localizedContentType(type)

@Composable
private fun localizedSourceLabel(source: String): String =
    com.duckylife.heritage.modern.ui.text.localizedReadingPathSource(source)

private fun formatSyncTime(timestamp: Long): String {
    return runCatching {
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.getDefault()),
            )
    }.getOrDefault("")
}

private fun formatViewedAt(viewedAt: String?, legacyLastViewedAt: Long?): String {
    val millis: Long = when {
        viewedAt != null -> {
            runCatching { Instant.parse(viewedAt).toEpochMilli() }.getOrNull()
                ?: legacyLastViewedAt
                ?: return ""
        }
        legacyLastViewedAt != null -> legacyLastViewedAt
        else -> return ""
    }
    return runCatching {
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.getDefault()),
            )
    }.getOrDefault("")
}

private fun formatIsoDate(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return runCatching {
        Instant.parse(iso)
            .atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    .withLocale(Locale.getDefault()),
            )
    }.getOrDefault(iso)
}

@Preview(showBackground = true)
@Composable
private fun MyPagePreview() {
    HeritageTheme {
        MyPage(
            onBack = {},
            onNavigate = {},
        )
    }
}

internal fun extractDisplayUrl(coverImageJson: String?): String? =
    extractCoverImageUrl(coverImageJson)
