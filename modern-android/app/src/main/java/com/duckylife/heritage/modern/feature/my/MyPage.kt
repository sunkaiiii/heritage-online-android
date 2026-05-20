package com.duckylife.heritage.modern.feature.my

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.ui.component.HeritageListCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MyPageDestination {
    data class Article(
        val articleId: String?,
        val sourceId: String?,
        val sourceUrl: String?,
        val category: ArticleCategory,
    ) : MyPageDestination

    data class Directory(
        val itemId: String?,
        val sourceId: String?,
        val kind: DirectoryItemKind,
    ) : MyPageDestination

    data class Inheritor(
        val inheritorId: String?,
        val sourceId: String?,
    ) : MyPageDestination
}

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val savedContentRepository: SavedContentRepository,
) : ViewModel() {
    val favorites: StateFlow<List<SavedContentEntity>> =
        savedContentRepository.favorites().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val recentlyViewed: StateFlow<List<SavedContentEntity>> =
        savedContentRepository.recentlyViewed().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun unfavorite(entity: SavedContentEntity) {
        viewModelScope.launch {
            savedContentRepository.removeFavorite(entity.toTarget())
        }
    }

    fun removeRecent(entity: SavedContentEntity) {
        viewModelScope.launch {
            savedContentRepository.removeRecent(entity.toTarget())
        }
    }

    fun clearRecent() {
        viewModelScope.launch {
            savedContentRepository.clearRecent()
        }
    }

    private fun SavedContentEntity.toTarget() = SavedContentTarget(
        id = targetId,
        sourceId = targetSourceId,
        sourceUrl = targetSourceUrl,
        category = targetCategory,
        kind = targetKind,
    )
}

@Composable
fun MyPage(
    onBack: () -> Unit,
    onNavigate: (MyPageDestination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val recentlyViewed by viewModel.recentlyViewed.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showClearRecentDialog by remember { mutableStateOf(false) }
    val imageLoader = rememberHeritageImageLoader()

    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = stringResource(R.string.my_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            SecondaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.favorites_tab)) },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.recent_tab)) },
                )
            }

            val items = if (selectedTab == 0) favorites else recentlyViewed
            val emptyMessage = if (selectedTab == 0) {
                stringResource(R.string.favorites_empty_message)
            } else {
                stringResource(R.string.recent_empty_message)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // 最近浏览非空时显示"清空最近浏览"
                if (selectedTab == 1 && items.isNotEmpty()) {
                    item {
                        TextButton(onClick = { showClearRecentDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.ClearAll,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(stringResource(R.string.action_clear_recent))
                        }
                    }
                }
                if (items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = emptyMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                items(items, key = { it.contentKey }) { entity ->
                    SavedContentRow(
                        entity = entity,
                        imageLoader = imageLoader,
                        isFavoriteTab = selectedTab == 0,
                        onClick = {
                            val dest = entity.toDestination() ?: return@SavedContentRow
                            onNavigate(dest)
                        },
                        onRemove = {
                            if (selectedTab == 0) viewModel.unfavorite(entity)
                            else viewModel.removeRecent(entity)
                        },
                    )
                }
            }
        }
    }

    if (showClearRecentDialog) {
        AlertDialog(
            onDismissRequest = { showClearRecentDialog = false },
            title = { Text(stringResource(R.string.action_clear_recent)) },
            text = { Text(stringResource(R.string.action_clear_recent_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearRecent()
                    showClearRecentDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearRecentDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun SavedContentRow(
    entity: SavedContentEntity,
    imageLoader: ImageLoader,
    isFavoriteTab: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val fallbackText = stringResource(R.string.brand_fallback)
    val removeLabel = if (isFavoriteTab) stringResource(R.string.action_unfavorite)
    else stringResource(R.string.action_remove_recent)
    Column {
        HeritageListCard(
            onClick = onClick,
            image = {
                HeritageListImage(
                    imageUrl = entity.coverImageJson?.let { extractDisplayUrl(it) },
                    imageLoader = imageLoader,
                    fallbackText = fallbackText,
                    modifier = Modifier
                        .size(width = 72.dp, height = 72.dp)
                        .clip(RoundedCornerShape(6.dp)),
                )
            },
            text = {
                Text(
                    text = entity.title.orEmpty().ifBlank {
                        stringResource(
                            when (entity.contentType) {
                                "inheritor" -> R.string.unnamed_inheritor
                                "directoryItem" -> R.string.unnamed_directory_item
                                else -> R.string.unnamed_article
                            },
                        )
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!entity.summary.isNullOrBlank()) {
                    Text(
                        text = entity.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
        )
        TextButton(
            onClick = onRemove,
            modifier = Modifier.padding(start = 4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        ) {
            Icon(
                imageVector = if (isFavoriteTab) Icons.Outlined.Favorite else Icons.Outlined.DeleteOutline,
                contentDescription = removeLabel,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = removeLabel,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

internal fun extractDisplayUrl(coverImageJson: String?): String? {
    if (coverImageJson.isNullOrBlank()) return null
    return runCatching {
        val asset = HeritageJson.decodeFromString<MediaAssetDto>(coverImageJson)
        asset.displayUrl ?: asset.thumbnailUrl
    }.getOrNull()
}

private fun SavedContentEntity.toDestination(): MyPageDestination? {
    return when (contentType) {
        "article" -> MyPageDestination.Article(
            articleId = targetId,
            sourceId = targetSourceId,
            sourceUrl = targetSourceUrl,
            category = ArticleCategory.entries.firstOrNull { it.wireName == targetCategory } ?: ArticleCategory.News,
        )
        "directoryItem" -> MyPageDestination.Directory(
            itemId = targetId,
            sourceId = targetSourceId,
            kind = DirectoryItemKind.entries.firstOrNull { it.wireName == targetKind } ?: DirectoryItemKind.NationalProject,
        )
        "inheritor" -> MyPageDestination.Inheritor(
            inheritorId = targetId,
            sourceId = targetSourceId,
        )
        else -> null
    }
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
