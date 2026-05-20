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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.duckylife.heritage.modern.ui.component.HeritageListCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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
    savedContentRepository: SavedContentRepository,
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
                        onClick = {
                            val dest = entity.toDestination() ?: return@SavedContentRow
                            onNavigate(dest)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedContentRow(
    entity: SavedContentEntity,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
) {
    val fallbackText = stringResource(R.string.brand_fallback)
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
}

private fun extractDisplayUrl(coverImageJson: String): String? {
    return try {
        // Simple extraction of displayUrl from JSON without full deserialization
        val key = "\"displayUrl\":\""
        val start = coverImageJson.indexOf(key)
        if (start < 0) {
            val key2 = "\"thumbnailUrl\":\""
            val start2 = coverImageJson.indexOf(key2)
            if (start2 < 0) return null
            val begin = start2 + key2.length
            val end = coverImageJson.indexOf('"', begin)
            if (end < 0) return null
            coverImageJson.substring(begin, end).replace("\\/", "/")
        } else {
            val begin = start + key.length
            val end = coverImageJson.indexOf('"', begin)
            if (end < 0) return null
            coverImageJson.substring(begin, end).replace("\\/", "/")
        }
    } catch (_: Exception) {
        null
    }
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
