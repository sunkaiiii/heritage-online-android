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
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute
import com.duckylife.heritage.modern.feature.collections.CollectionRoute
import com.duckylife.heritage.modern.feature.compare.CompareRoute
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.discovery.deepdive.DeepDiveRoute
import com.duckylife.heritage.modern.feature.explore.ExploreTopicRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.feature.learning.LearningPathRoute
import com.duckylife.heritage.modern.feature.regions.RegionAtlasRoute
import com.duckylife.heritage.modern.feature.regions.RegionDetailRoute
import com.duckylife.heritage.modern.feature.search.SearchRoute
import com.duckylife.heritage.modern.feature.stories.StoriesIndexRoute
import com.duckylife.heritage.modern.feature.stories.StoryRoute
import com.duckylife.heritage.modern.feature.taxonomy.TaxonomyDetailRoute
import com.duckylife.heritage.modern.feature.taxonomy.TaxonomyRoute
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

private data object TaxonomyPage

private data class TaxonomyDetailPage(val type: String, val key: String)

private data class ComparePage(
    val type: String? = null,
    val left: String? = null,
    val right: String? = null,
)

private data object StoriesIndexPage

private data class StoryPage(
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
)

private data class DeepDivePage(val seedType: String, val seedId: String)

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
// Serializable route state (safe for process restore)
// ---------------------------------------------------------------------------

@kotlinx.serialization.Serializable
private sealed interface RouteState {
    @kotlinx.serialization.Serializable data object Index : RouteState
    @kotlinx.serialization.Serializable data class Search(val query: String = "") : RouteState
    @kotlinx.serialization.Serializable data class ExploreTopic(val type: String = "", val key: String = "") : RouteState
    @kotlinx.serialization.Serializable data class LearningPath(val id: String = "") : RouteState
    @kotlinx.serialization.Serializable data class Collection(val id: String? = null, val type: String? = null, val key: String? = null) : RouteState
    @kotlinx.serialization.Serializable data object RegionAtlas : RouteState
    @kotlinx.serialization.Serializable data class RegionDetail(val region: String = "") : RouteState
    @kotlinx.serialization.Serializable data object Timeline : RouteState
    @kotlinx.serialization.Serializable data object Taxonomy : RouteState
    @kotlinx.serialization.Serializable data class TaxonomyDetail(val type: String = "", val key: String = "") : RouteState
    @kotlinx.serialization.Serializable data class Compare(val type: String? = null, val left: String? = null, val right: String? = null) : RouteState
    @kotlinx.serialization.Serializable data object StoriesIndex : RouteState
    @kotlinx.serialization.Serializable data class Story(val region: String? = null, val category: String? = null, val year: Int? = null) : RouteState
    @kotlinx.serialization.Serializable data class DeepDive(val seedType: String = "", val seedId: String = "") : RouteState
    @kotlinx.serialization.Serializable data class ArticleDetail(val id: String? = null, val sourceId: String? = null, val sourceUrl: String? = null, val category: String = "news") : RouteState
    @kotlinx.serialization.Serializable data class DirectoryDetail(val id: String? = null, val sourceId: String? = null, val kind: String = "nationalProject") : RouteState
    @kotlinx.serialization.Serializable data class InheritorDetail(val id: String? = null, val sourceId: String? = null) : RouteState
}

private fun Any.toRouteState(): RouteState = when (this) {
    is DiscoveryIndex -> RouteState.Index
    is SearchResults -> RouteState.Search(query)
    is ExploreTopicDetail -> RouteState.ExploreTopic(type, key)
    is LearningPathDetail -> RouteState.LearningPath(id)
    is CollectionDetail -> RouteState.Collection(id, type, key)
    is RegionAtlasPage -> RouteState.RegionAtlas
    is RegionDetailPage -> RouteState.RegionDetail(region)
    is TimelinePage -> RouteState.Timeline
    is TaxonomyPage -> RouteState.Taxonomy
    is TaxonomyDetailPage -> RouteState.TaxonomyDetail(type, key)
    is ComparePage -> RouteState.Compare(type, left, right)
    is StoriesIndexPage -> RouteState.StoriesIndex
    is StoryPage -> RouteState.Story(region, category, year)
    is DeepDivePage -> RouteState.DeepDive(seedType, seedId)
    is DiscoveryArticleDetail -> RouteState.ArticleDetail(id, sourceId, sourceUrl, category.wireName)
    is DiscoveryDirectoryDetail -> RouteState.DirectoryDetail(id, sourceId, kind.wireName)
    is DiscoveryInheritorDetail -> RouteState.InheritorDetail(id, sourceId)
    else -> RouteState.Index
}

private fun RouteState.toRouteKey(): Any = when (this) {
    is RouteState.Index -> DiscoveryIndex
    is RouteState.Search -> SearchResults(query)
    is RouteState.ExploreTopic -> ExploreTopicDetail(type, key)
    is RouteState.LearningPath -> LearningPathDetail(id)
    is RouteState.Collection -> CollectionDetail(id, type, key)
    is RouteState.RegionAtlas -> RegionAtlasPage
    is RouteState.RegionDetail -> RegionDetailPage(region)
    is RouteState.Timeline -> TimelinePage
    is RouteState.Taxonomy -> TaxonomyPage
    is RouteState.TaxonomyDetail -> TaxonomyDetailPage(type, key)
    is RouteState.Compare -> ComparePage(type, left, right)
    is RouteState.StoriesIndex -> StoriesIndexPage
    is RouteState.Story -> StoryPage(region, category, year)
    is RouteState.DeepDive -> DeepDivePage(seedType, seedId)
    is RouteState.ArticleDetail -> DiscoveryArticleDetail(
        id = id, sourceId = sourceId, sourceUrl = sourceUrl,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
    )
    is RouteState.DirectoryDetail -> DiscoveryDirectoryDetail(
        id = id, sourceId = sourceId,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName == kind } ?: DirectoryItemKind.NationalProject,
    )
    is RouteState.InheritorDetail -> DiscoveryInheritorDetail(id = id, sourceId = sourceId)
}

private fun serializeDiscovery(stack: List<Any>): String =
    try {
        HeritageJson.encodeToString(stack.map { it.toRouteState() })
    } catch (_: Exception) {
        "I"
    }

private fun deserializeDiscovery(str: String): List<Any> =
    try {
        if (str.isBlank()) listOf(DiscoveryIndex)
        else HeritageJson.decodeFromString<List<RouteState>>(str).map { it.toRouteKey() }
    } catch (_: Exception) {
        listOf(DiscoveryIndex)
    }

// ---------------------------------------------------------------------------
// Helper: type-based routing for DiscoveryItemDto
// ---------------------------------------------------------------------------

private fun navigateToDiscoveryItem(
    item: DiscoveryItemDto,
    backStack: MutableList<Any>,
) {
    val id = item.id
    if (id.isNullOrBlank()) return
    when (item.type) {
        "directoryItem" -> backStack.add(DiscoveryDirectoryDetail(id = id))
        "inheritor" -> backStack.add(DiscoveryInheritorDetail(id = id))
        else -> backStack.add(DiscoveryArticleDetail(id = id))
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
                        onTrendingItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onWeeklyItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onTodayItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onDeepDiveClick = { item ->
                            if (!item.id.isNullOrBlank()) {
                                backStack.add(DeepDivePage(seedType = item.type, seedId = item.id))
                            }
                        },
                        onTaxonomyClick = {
                            backStack.add(TaxonomyPage)
                        },
                        onStoriesClick = {
                            backStack.add(StoriesIndexPage)
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

                // ---- Taxonomy ----
                is TaxonomyPage -> NavEntry(key) {
                    TaxonomyRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onTopicClick = { type, topicKey ->
                            if (type == "kind") {
                                // Kind 没有详情 API，直接进入对比页
                                backStack.add(ComparePage(type = "kind", left = topicKey))
                            } else {
                                backStack.add(TaxonomyDetailPage(type = type, key = topicKey))
                            }
                        },
                        modifier = modifier,
                    )
                }

                // ---- Taxonomy Detail ----
                is TaxonomyDetailPage -> NavEntry(key) {
                    TaxonomyDetailRoute(
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
                        onRelatedTopicClick = { type, topicKey ->
                            backStack.add(TaxonomyDetailPage(type = type, key = topicKey))
                        },
                        onViewStory = {
                            when (key.type) {
                                "category" -> backStack.add(StoryPage(category = key.key))
                                "region" -> backStack.add(StoryPage(region = key.key))
                            }
                        },
                        onCompare = {
                            when (key.type) {
                                "category" -> backStack.add(ComparePage(type = "category", left = key.key))
                                "region" -> backStack.add(ComparePage(type = "region", left = key.key))
                            }
                        },
                        onCollectionSelected = { collectionId ->
                            backStack.add(CollectionDetail(id = collectionId))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Compare ----
                is ComparePage -> NavEntry(key) {
                    CompareRoute(
                        initialType = key.type,
                        initialLeft = key.left,
                        initialRight = key.right,
                        onBack = { backStack.removeLastOrNull() },
                        onItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        modifier = modifier,
                    )
                }

                // ---- Stories Index ----
                is StoriesIndexPage -> NavEntry(key) {
                    StoriesIndexRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onRegionStoryClick = { region ->
                            backStack.add(StoryPage(region = region))
                        },
                        onCategoryStoryClick = { category ->
                            backStack.add(StoryPage(category = category))
                        },
                        onYearStoryClick = { year ->
                            backStack.add(StoryPage(year = year))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Story ----
                is StoryPage -> NavEntry(key) {
                    StoryRoute(
                        region = key.region,
                        category = key.category,
                        year = key.year,
                        onBack = { backStack.removeLastOrNull() },
                        onItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onTopicClick = { type, topicKey ->
                            backStack.add(ExploreTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Deep Dive ----
                is DeepDivePage -> NavEntry(key) {
                    DeepDiveRoute(
                        seedType = key.seedType,
                        seedId = key.seedId,
                        onBack = { backStack.removeLastOrNull() },
                        onItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
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
                        onTrendingItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onWeeklyItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onTodayItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onDeepDiveClick = { item ->
                            if (!item.id.isNullOrBlank()) {
                                backStack.add(DeepDivePage(seedType = item.type, seedId = item.id))
                            }
                        },
                        onTaxonomyClick = {
                            backStack.add(TaxonomyPage)
                        },
                        onStoriesClick = {
                            backStack.add(StoriesIndexPage)
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}
