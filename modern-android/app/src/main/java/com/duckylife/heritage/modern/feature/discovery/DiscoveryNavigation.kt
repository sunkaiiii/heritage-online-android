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
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute
import com.duckylife.heritage.modern.feature.collections.CollectionRoute
import com.duckylife.heritage.modern.feature.compare.CompareRoute
import com.duckylife.heritage.modern.feature.detail.DetailContextTarget
import com.duckylife.heritage.modern.feature.discovery.graphexplore.GraphExploreRoute
import com.duckylife.heritage.modern.feature.my.MyPageDestination
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.discovery.deepdive.DeepDiveRoute
import com.duckylife.heritage.modern.feature.explore.ExploreTopicRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.feature.learning.LearningPathRoute
import com.duckylife.heritage.modern.feature.learningroutes.LearningRouteDetailRoute
import com.duckylife.heritage.modern.feature.learningroutes.LearningRoutesRoute
import com.duckylife.heritage.modern.feature.regions.RegionAtlasRoute
import com.duckylife.heritage.modern.feature.regions.RegionDetailRoute
import com.duckylife.heritage.modern.feature.search.SearchRoute
import com.duckylife.heritage.modern.feature.stories.StoriesIndexRoute
import com.duckylife.heritage.modern.feature.stories.StoryRoute
import com.duckylife.heritage.modern.feature.taxonomy.TaxonomyDetailRoute
import com.duckylife.heritage.modern.feature.taxonomy.TaxonomyRoute
import com.duckylife.heritage.modern.feature.timeline.TimelineRoute

// NavDisplay key 的智能转换辅助函数
private fun Any.asRouteKey(): DiscoveryRouteKey? = this as? DiscoveryRouteKey

// ---------------------------------------------------------------------------
// 辅助函数
// ---------------------------------------------------------------------------

private fun navigateToDiscoveryItem(
    item: DiscoveryItemDto,
    backStack: MutableList<Any>,
) {
    val id = item.id
    if (id.isNullOrBlank()) return
    when (item.type) {
        "article" -> backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
        "directoryItem" -> backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
        "inheritor" -> backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
        else -> return // 未知类型不导航，避免错误跳转
    }
}

private fun navigateToDetailContextTarget(
    target: DetailContextTarget,
    backStack: MutableList<Any>,
) {
    when (target) {
        is DetailContextTarget.Article ->
            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = target.id))
        is DetailContextTarget.DirectoryItem ->
            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = target.id))
        is DetailContextTarget.Inheritor ->
            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = target.id))
        is DetailContextTarget.Collection ->
            backStack.add(DiscoveryRouteKey.CollectionDetail(id = target.id))
        is DetailContextTarget.Topic ->
            backStack.add(DiscoveryRouteKey.ExploreTopicDetail(type = target.type, key = target.key))
    }
}

private fun DiscoveryRouteKey.DiscoveryArticleDetail.continueExploreContentId(): String? =
    id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private fun DiscoveryRouteKey.DiscoveryDirectoryDetail.continueExploreContentId(): String? =
    id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private fun DiscoveryRouteKey.DiscoveryInheritorDetail.continueExploreContentId(): String? =
    id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

// ---------------------------------------------------------------------------
// NavHost
// ---------------------------------------------------------------------------

@Composable
fun DiscoveryNavHost(
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    pendingNavigation: MyPageDestination.GraphExplore? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("") }
    val backStack = remember {
        mutableStateListOf<Any>().also {
            it.addAll(deserializeDiscoveryRoutes(savedStack))
        }
    }
    LaunchedEffect(backStack.size) {
        savedStack = serializeDiscoveryRoutes(backStack.filterIsInstance<DiscoveryRouteKey>())
    }
    val isInDetail = backStack.lastOrNull() !is DiscoveryRouteKey.DiscoveryIndex
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }

    LaunchedEffect(pendingNavigation) {
        val destination = pendingNavigation ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(DiscoveryRouteKey.DiscoveryIndex)
        backStack.add(
            DiscoveryRouteKey.GraphExplorePage(
                type = destination.contentType,
                contentId = destination.contentId,
                initialTab = GraphTab.Similar,
            ),
        )
        onPendingNavigationConsumed()
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider@{ entryKey ->
            val key = entryKey.asRouteKey() ?: DiscoveryRouteKey.DiscoveryIndex
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            when (key) {
                // ---- Discovery Index ----
                is DiscoveryRouteKey.DiscoveryIndex -> NavEntry(entryKey) {
                    DiscoveryRoute(
                        onSearchSubmit = { query ->
                            backStack.add(DiscoveryRouteKey.SearchResults(query = query))
                        },
                        onTopicClick = { topic ->
                            if (!topic.type.isNullOrBlank() && !topic.key.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.ExploreTopicDetail(type = topic.type, key = topic.key))
                            }
                        },
                        onLearningPathClick = { path ->
                            if (!path.id.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.LearningPathDetail(id = path.id))
                            }
                        },
                        onCollectionClick = { collection ->
                            if (!collection.id.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.CollectionDetail(id = collection.id))
                            }
                        },
                        onRegionAtlasClick = {
                            backStack.add(DiscoveryRouteKey.RegionAtlasPage)
                        },
                        onTimelineClick = {
                            backStack.add(DiscoveryRouteKey.TimelinePage)
                        },
                        onTrendingItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onWeeklyItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onTodayItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onDeepDiveClick = { item ->
                            if (!item.id.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.DeepDivePage(seedType = item.type, seedId = item.id))
                            }
                        },
                        onTaxonomyClick = {
                            backStack.add(DiscoveryRouteKey.TaxonomyPage)
                        },
                        onStoriesClick = {
                            backStack.add(DiscoveryRouteKey.StoriesIndexPage)
                        },
                        modifier = modifier,
                    )
                }

                // ---- Search ----
                is DiscoveryRouteKey.SearchResults -> NavEntry(entryKey) {
                    SearchRoute(
                        initialQuery = key.query,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Explore Topic ----
                is DiscoveryRouteKey.ExploreTopicDetail -> NavEntry(entryKey) {
                    ExploreTopicRoute(
                        type = key.type,
                        key = key.key,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(DiscoveryRouteKey.ExploreTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Learning Path ----
                is DiscoveryRouteKey.LearningPathDetail -> NavEntry(entryKey) {
                    LearningPathRoute(
                        id = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(DiscoveryRouteKey.ExploreTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Learning Routes ----
                is DiscoveryRouteKey.LearningRoutesPage -> NavEntry(entryKey) {
                    LearningRoutesRoute(
                        seedType = key.seedType,
                        seedId = key.seedId,
                        onBack = { backStack.removeLastOrNull() },
                        modifier = modifier,
                    )
                }

                // ---- Learning Route Detail ----
                is DiscoveryRouteKey.LearningRouteDetailPage -> NavEntry(entryKey) {
                    LearningRouteDetailRoute(
                        routeId = key.routeId,
                        onBack = { backStack.removeLastOrNull() },
                        modifier = modifier,
                    )
                }

                // ---- Collection ----
                is DiscoveryRouteKey.CollectionDetail -> NavEntry(entryKey) {
                    CollectionRoute(
                        id = key.id,
                        type = key.type,
                        topicKey = key.key,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Region Atlas ----
                is DiscoveryRouteKey.RegionAtlasPage -> NavEntry(entryKey) {
                    RegionAtlasRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onRegionSelected = { region ->
                            backStack.add(DiscoveryRouteKey.RegionDetailPage(region = region))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Region Detail ----
                is DiscoveryRouteKey.RegionDetailPage -> NavEntry(entryKey) {
                    RegionDetailRoute(
                        region = key.region,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedRegionSelected = { region ->
                            backStack.add(DiscoveryRouteKey.RegionDetailPage(region = region))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Timeline ----
                is DiscoveryRouteKey.TimelinePage -> NavEntry(entryKey) {
                    TimelineRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Article Detail ----
                is DiscoveryRouteKey.DiscoveryArticleDetail -> NavEntry(entryKey) {
                    val contentId = key.continueExploreContentId()
                    ArticleDetailRoute(
                        articleId = key.id,
                        sourceId = key.sourceId,
                        sourceUrl = key.sourceUrl,
                        category = key.category,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedArticleSelected = { reference, category ->
                            backStack.add(
                                DiscoveryRouteKey.DiscoveryArticleDetail(
                                    sourceId = reference.sourceId,
                                    sourceUrl = reference.detailUrl,
                                    category = category,
                                ),
                            )
                        },
                        onExploreTargetClick = { click ->
                            navigateToDetailContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.GraphExplorePage(
                                        type = "article",
                                        contentId = it,
                                        initialTab = GraphTab.Neighbors,
                                    ),
                                )
                            }
                        },
                        onSimilarClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.GraphExplorePage(
                                        type = "article",
                                        contentId = it,
                                        initialTab = GraphTab.Similar,
                                    ),
                                )
                            }
                        },
                        onLearningRoutesClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.LearningRoutesPage(
                                        seedType = "article",
                                        seedId = it,
                                    ),
                                )
                            }
                        },
                        modifier = modifier,
                    )
                }

                // ---- Directory Detail ----
                is DiscoveryRouteKey.DiscoveryDirectoryDetail -> NavEntry(entryKey) {
                    val contentId = key.continueExploreContentId()
                    DirectoryDetailRoute(
                        itemId = key.id,
                        sourceId = key.sourceId,
                        kind = key.kind,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference, kind ->
                            backStack.add(
                                DiscoveryRouteKey.DiscoveryDirectoryDetail(
                                    sourceId = reference.sourceId,
                                    kind = kind,
                                ),
                            )
                        },
                        onRelatedInheritorSelected = { reference ->
                            backStack.add(
                                DiscoveryRouteKey.DiscoveryInheritorDetail(
                                    sourceId = reference.sourceId,
                                ),
                            )
                        },
                        onExploreTargetClick = { click ->
                            navigateToDetailContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.GraphExplorePage(
                                        type = "directoryItem",
                                        contentId = it,
                                        initialTab = GraphTab.Neighbors,
                                    ),
                                )
                            }
                        },
                        onSimilarClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.GraphExplorePage(
                                        type = "directoryItem",
                                        contentId = it,
                                        initialTab = GraphTab.Similar,
                                    ),
                                )
                            }
                        },
                        onLearningRoutesClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.LearningRoutesPage(
                                        seedType = "directoryItem",
                                        seedId = it,
                                    ),
                                )
                            }
                        },
                        modifier = modifier,
                    )
                }

                // ---- Inheritor Detail ----
                is DiscoveryRouteKey.DiscoveryInheritorDetail -> NavEntry(entryKey) {
                    val contentId = key.continueExploreContentId()
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference ->
                            backStack.add(
                                DiscoveryRouteKey.DiscoveryDirectoryDetail(
                                    sourceId = reference.sourceId,
                                ),
                            )
                        },
                        onRelatedInheritorSelected = { reference ->
                            backStack.add(
                                DiscoveryRouteKey.DiscoveryInheritorDetail(
                                    sourceId = reference.sourceId,
                                ),
                            )
                        },
                        onExploreTargetClick = { click ->
                            navigateToDetailContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.GraphExplorePage(
                                        type = "inheritor",
                                        contentId = it,
                                        initialTab = GraphTab.Neighbors,
                                    ),
                                )
                            }
                        },
                        onSimilarClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.GraphExplorePage(
                                        type = "inheritor",
                                        contentId = it,
                                        initialTab = GraphTab.Similar,
                                    ),
                                )
                            }
                        },
                        onLearningRoutesClick = {
                            contentId?.let {
                                backStack.add(
                                    DiscoveryRouteKey.LearningRoutesPage(
                                        seedType = "inheritor",
                                        seedId = it,
                                    ),
                                )
                            }
                        },
                        modifier = modifier,
                    )
                }

                // ---- Taxonomy ----
                is DiscoveryRouteKey.TaxonomyPage -> NavEntry(entryKey) {
                    TaxonomyRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onTopicClick = { type, topicKey ->
                            if (type == "kind") {
                                backStack.add(DiscoveryRouteKey.ComparePage(type = "kind", left = topicKey))
                            } else {
                                backStack.add(DiscoveryRouteKey.TaxonomyDetailPage(type = type, key = topicKey))
                            }
                        },
                        modifier = modifier,
                    )
                }

                // ---- Taxonomy Detail ----
                is DiscoveryRouteKey.TaxonomyDetailPage -> NavEntry(entryKey) {
                    TaxonomyDetailRoute(
                        type = key.type,
                        key = key.key,
                        onBack = { backStack.removeLastOrNull() },
                        onArticleSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DiscoveryRouteKey.DiscoveryInheritorDetail(id = id))
                        },
                        onRelatedTopicClick = { type, topicKey ->
                            backStack.add(DiscoveryRouteKey.TaxonomyDetailPage(type = type, key = topicKey))
                        },
                        onViewStory = {
                            when (key.type) {
                                "category" -> backStack.add(DiscoveryRouteKey.StoryPage(category = key.key))
                                "region" -> backStack.add(DiscoveryRouteKey.StoryPage(region = key.key))
                            }
                        },
                        onCompare = {
                            when (key.type) {
                                "category" -> backStack.add(DiscoveryRouteKey.ComparePage(type = "category", left = key.key))
                                "region" -> backStack.add(DiscoveryRouteKey.ComparePage(type = "region", left = key.key))
                            }
                        },
                        onCollectionSelected = { collectionId ->
                            backStack.add(DiscoveryRouteKey.CollectionDetail(id = collectionId))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Compare ----
                is DiscoveryRouteKey.ComparePage -> NavEntry(entryKey) {
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
                is DiscoveryRouteKey.StoriesIndexPage -> NavEntry(entryKey) {
                    StoriesIndexRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onRegionStoryClick = { region ->
                            backStack.add(DiscoveryRouteKey.StoryPage(region = region))
                        },
                        onCategoryStoryClick = { category ->
                            backStack.add(DiscoveryRouteKey.StoryPage(category = category))
                        },
                        onYearStoryClick = { year ->
                            backStack.add(DiscoveryRouteKey.StoryPage(year = year))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Story ----
                is DiscoveryRouteKey.StoryPage -> NavEntry(entryKey) {
                    StoryRoute(
                        region = key.region,
                        category = key.category,
                        year = key.year,
                        onBack = { backStack.removeLastOrNull() },
                        onItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onTopicClick = { type, topicKey ->
                            backStack.add(DiscoveryRouteKey.ExploreTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                // ---- Deep Dive ----
                is DiscoveryRouteKey.DeepDivePage -> NavEntry(entryKey) {
                    DeepDiveRoute(
                        seedType = key.seedType,
                        seedId = key.seedId,
                        onBack = { backStack.removeLastOrNull() },
                        onItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        modifier = modifier,
                    )
                }

                // ---- Graph Explore ----
                is DiscoveryRouteKey.GraphExplorePage -> NavEntry(entryKey) {
                    GraphExploreRoute(
                        contentType = key.type,
                        contentId = key.contentId,
                        initialTab = key.initialTab,
                        onBack = { backStack.removeLastOrNull() },
                        onItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        modifier = modifier,
                    )
                }

                // ---- Fallback ----
                else -> NavEntry(Unit) {
                    DiscoveryRoute(
                        onSearchSubmit = { query ->
                            backStack.add(DiscoveryRouteKey.SearchResults(query = query))
                        },
                        onTopicClick = { topic ->
                            if (!topic.type.isNullOrBlank() && !topic.key.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.ExploreTopicDetail(type = topic.type, key = topic.key))
                            }
                        },
                        onLearningPathClick = { path ->
                            if (!path.id.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.LearningPathDetail(id = path.id))
                            }
                        },
                        onCollectionClick = { collection ->
                            if (!collection.id.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.CollectionDetail(id = collection.id))
                            }
                        },
                        onRegionAtlasClick = {
                            backStack.add(DiscoveryRouteKey.RegionAtlasPage)
                        },
                        onTimelineClick = {
                            backStack.add(DiscoveryRouteKey.TimelinePage)
                        },
                        onTrendingItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onWeeklyItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onTodayItemClick = { item -> navigateToDiscoveryItem(item, backStack) },
                        onDeepDiveClick = { item ->
                            if (!item.id.isNullOrBlank()) {
                                backStack.add(DiscoveryRouteKey.DeepDivePage(seedType = item.type, seedId = item.id))
                            }
                        },
                        onTaxonomyClick = {
                            backStack.add(DiscoveryRouteKey.TaxonomyPage)
                        },
                        onStoriesClick = {
                            backStack.add(DiscoveryRouteKey.StoriesIndexPage)
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}
