package com.duckylife.heritage.modern.feature.discovery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute
import com.duckylife.heritage.modern.feature.collections.CollectionRoute
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.explore.ExploreTopicRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.feature.learning.LearningPathRoute
import com.duckylife.heritage.modern.feature.regions.RegionAtlasRoute
import com.duckylife.heritage.modern.feature.regions.RegionDetailRoute
import com.duckylife.heritage.modern.feature.search.SearchRoute
import com.duckylife.heritage.modern.feature.timeline.TimelineRoute

// ---------------------------------------------------------------------------
// Route keys
// ---------------------------------------------------------------------------

private data object DiscoveryIndex

private data class SearchResults(val query: String)

private data class ExploreTopicDetail(val type: String, val key: String)

private data class LearningPathDetail(val id: String)

private data class CollectionDetail(
    val id: String? = null,
    val type: String? = null,
    val key: String? = null,
)

private data object RegionAtlasPage

private data class RegionDetailPage(val region: String)

private data object TimelinePage

private data class DiscoveryArticleDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
)

private data class DiscoveryDirectoryDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

private data class DiscoveryInheritorDetail(
    val id: String? = null,
    val sourceId: String? = null,
)

// ---------------------------------------------------------------------------
// Serialize / Deserialize
// ---------------------------------------------------------------------------

private fun serializeDiscovery(stack: List<Any>): String =
    stack.joinToString("\n") { entry ->
        when (entry) {
            is DiscoveryIndex -> "I"
            is SearchResults -> "S|${entry.query}"
            is ExploreTopicDetail -> "ET|${entry.type}|${entry.key}"
            is LearningPathDetail -> "LP|${entry.id}"
            is CollectionDetail -> "C|${entry.id.orEmpty()}|${entry.type.orEmpty()}|${entry.key.orEmpty()}"
            is RegionAtlasPage -> "RA"
            is RegionDetailPage -> "RD|${entry.region}"
            is TimelinePage -> "T"
            is DiscoveryArticleDetail -> "DA|${entry.id.orEmpty()}|${entry.sourceId.orEmpty()}|${entry.sourceUrl.orEmpty()}|${entry.category.wireName}"
            is DiscoveryDirectoryDetail -> "DD|${entry.id.orEmpty()}|${entry.sourceId.orEmpty()}|${entry.kind.wireName}"
            is DiscoveryInheritorDetail -> "DI|${entry.id.orEmpty()}|${entry.sourceId.orEmpty()}"
            else -> "I"
        }
    }

private fun deserializeDiscovery(str: String): List<Any> =
    if (str.isBlank()) listOf(DiscoveryIndex)
    else str.split("\n").mapNotNull { item ->
        val parts = item.split("|")
        when (parts[0]) {
            "I" -> DiscoveryIndex
            "S" -> SearchResults(query = parts.getOrNull(1).orEmpty())
            "ET" -> ExploreTopicDetail(
                type = parts.getOrNull(1).orEmpty(),
                key = parts.getOrNull(2).orEmpty(),
            )
            "LP" -> LearningPathDetail(id = parts.getOrNull(1).orEmpty())
            "C" -> CollectionDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                type = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
                key = parts.getOrNull(3)?.takeIf { it.isNotEmpty() },
            )
            "RA" -> RegionAtlasPage
            "RD" -> RegionDetailPage(region = parts.getOrNull(1).orEmpty())
            "T" -> TimelinePage
            "DA" -> DiscoveryArticleDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                sourceId = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
                sourceUrl = parts.getOrNull(3)?.takeIf { it.isNotEmpty() },
                category = ArticleCategory.entries.firstOrNull { it.wireName == parts.getOrNull(4) } ?: ArticleCategory.News,
            )
            "DD" -> DiscoveryDirectoryDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                sourceId = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
                kind = DirectoryItemKind.entries.firstOrNull { it.wireName == parts.getOrNull(3) } ?: DirectoryItemKind.NationalProject,
            )
            "DI" -> DiscoveryInheritorDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                sourceId = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
            )
            else -> DiscoveryIndex
        }
    }

// ---------------------------------------------------------------------------
// NavHost
// ---------------------------------------------------------------------------

@Composable
fun DiscoveryNavHost(
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("I") }
    val backStack = remember { mutableStateListOf<Any>().also { it.addAll(deserializeDiscovery(savedStack)) } }
    LaunchedEffect(backStack.size) {
        savedStack = serializeDiscovery(backStack.toList())
    }
    val isInDetail = backStack.lastOrNull() !is DiscoveryIndex
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when (key) {
                // ---- Discovery Index ----
                DiscoveryIndex -> NavEntry(key) {
                    DiscoveryRoute(
                        onSearchSubmit = { query ->
                            backStack.add(SearchResults(query = query))
                        },
                        onTopicClick = { topic ->
                            if (!topic.type.isNullOrBlank() && !topic.key.isNullOrBlank()) {
                                backStack.add(ExploreTopicDetail(type = topic.type, key = topic.key))
                            }
                        },
                        onLearningPathClick = { path ->
                            if (!path.id.isNullOrBlank()) {
                                backStack.add(LearningPathDetail(id = path.id))
                            }
                        },
                        onCollectionClick = { collection ->
                            if (!collection.id.isNullOrBlank()) {
                                backStack.add(CollectionDetail(id = collection.id))
                            }
                        },
                        onRegionAtlasClick = {
                            backStack.add(RegionAtlasPage)
                        },
                        onTimelineClick = {
                            backStack.add(TimelinePage)
                        },
                        modifier = modifier,
                    )
                }

                // ---- Search ----
                is SearchResults -> NavEntry(key) {
                    SearchRoute(
                        initialQuery = key.query,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Explore Topic ----
                is ExploreTopicDetail -> NavEntry(key) {
                    ExploreTopicRoute(
                        type = key.type,
                        key = key.key,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(ExploreTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Learning Path ----
                is LearningPathDetail -> NavEntry(key) {
                    LearningPathRoute(
                        id = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(ExploreTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Collection ----
                is CollectionDetail -> NavEntry(key) {
                    CollectionRoute(
                        id = key.id,
                        type = key.type,
                        topicKey = key.key,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Region Atlas ----
                is RegionAtlasPage -> NavEntry(key) {
                    RegionAtlasRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onRegionSelected = { region ->
                            backStack.add(RegionDetailPage(region = region))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Region Detail ----
                is RegionDetailPage -> NavEntry(key) {
                    RegionDetailRoute(
                        region = key.region,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedRegionSelected = { region ->
                            backStack.add(RegionDetailPage(region = region))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Timeline ----
                is TimelinePage -> NavEntry(key) {
                    TimelineRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Article Detail (from Discovery) ----
                is DiscoveryArticleDetail -> NavEntry(key) {
                    ArticleDetailRoute(
                        articleId = key.id,
                        sourceId = key.sourceId,
                        sourceUrl = key.sourceUrl,
                        category = key.category,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedArticleSelected = { reference, category ->
                            backStack.add(
                                DiscoveryArticleDetail(
                                    sourceId = reference.sourceId,
                                    sourceUrl = reference.detailUrl,
                                    category = category,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                // ---- Directory Detail (from Discovery) ----
                is DiscoveryDirectoryDetail -> NavEntry(key) {
                    DirectoryDetailRoute(
                        itemId = key.id,
                        sourceId = key.sourceId,
                        kind = key.kind,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference, kind ->
                            backStack.add(
                                DiscoveryDirectoryDetail(
                                    sourceId = reference.sourceId,
                                    kind = kind,
                                ),
                            )
                        },
                        onRelatedInheritorSelected = { reference ->
                            backStack.add(
                                DiscoveryInheritorDetail(
                                    sourceId = reference.sourceId,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                // ---- Inheritor Detail (from Discovery) ----
                is DiscoveryInheritorDetail -> NavEntry(key) {
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference ->
                            backStack.add(
                                DiscoveryDirectoryDetail(
                                    sourceId = reference.sourceId,
                                ),
                            )
                        },
                        onRelatedInheritorSelected = { reference ->
                            backStack.add(
                                DiscoveryInheritorDetail(
                                    sourceId = reference.sourceId,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    DiscoveryRoute(
                        onSearchSubmit = { query ->
                            backStack.add(SearchResults(query = query))
                        },
                        onTopicClick = { topic ->
                            if (!topic.type.isNullOrBlank() && !topic.key.isNullOrBlank()) {
                                backStack.add(ExploreTopicDetail(type = topic.type, key = topic.key))
                            }
                        },
                        onLearningPathClick = { path ->
                            if (!path.id.isNullOrBlank()) {
                                backStack.add(LearningPathDetail(id = path.id))
                            }
                        },
                        onCollectionClick = { collection ->
                            if (!collection.id.isNullOrBlank()) {
                                backStack.add(CollectionDetail(id = collection.id))
                            }
                        },
                        onRegionAtlasClick = {
                            backStack.add(RegionAtlasPage)
                        },
                        onTimelineClick = {
                            backStack.add(TimelinePage)
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}
