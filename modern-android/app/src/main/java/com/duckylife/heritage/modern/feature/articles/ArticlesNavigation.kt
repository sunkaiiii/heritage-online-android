package com.duckylife.heritage.modern.feature.articles

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
import com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute
import com.duckylife.heritage.modern.feature.collections.CollectionRoute
import com.duckylife.heritage.modern.feature.detail.DetailContextRouteMapper
import com.duckylife.heritage.modern.feature.detail.DetailContextTarget
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.explore.ExploreTopicRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.feature.my.MyPageDestination

// 路由 key
private data object ArticlesList

private data class ArticleDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
)

// 跨内容类型路由（从 Context 点击进入）
private data class ArticleTabDirectoryDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

private data class ArticleTabInheritorDetail(
    val id: String? = null,
    val sourceId: String? = null,
)

private data class ArticleTabCollectionDetail(val id: String)

private data class ArticleTabTopicDetail(val type: String, val key: String)

private val ArticleDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private val ArticleTabDirectoryDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private val ArticleTabInheritorDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

// 序列化（JSON 安全）
@kotlinx.serialization.Serializable
private sealed interface ArticleRouteState {
    @kotlinx.serialization.Serializable data object List : ArticleRouteState
    @kotlinx.serialization.Serializable data class Detail(
        val id: String? = null,
        val sourceId: String? = null,
        val sourceUrl: String? = null,
        val category: String = "news",
    ) : ArticleRouteState
    @kotlinx.serialization.Serializable data class DirDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val kind: String = "nationalProject",
    ) : ArticleRouteState
    @kotlinx.serialization.Serializable data class InhDetail(
        val id: String? = null,
        val sourceId: String? = null,
    ) : ArticleRouteState
    @kotlinx.serialization.Serializable data class CollDetail(val id: String = "") : ArticleRouteState
    @kotlinx.serialization.Serializable data class TopicDetail(
        @kotlinx.serialization.SerialName("topicType") val type: String = "",
        val key: String = "",
    ) : ArticleRouteState
}

private fun Any.toArticleRouteState(): ArticleRouteState = when (this) {
    is ArticlesList -> ArticleRouteState.List
    is ArticleDetail -> ArticleRouteState.Detail(id, sourceId, sourceUrl, category.wireName)
    is ArticleTabDirectoryDetail -> ArticleRouteState.DirDetail(id, sourceId, kind.wireName)
    is ArticleTabInheritorDetail -> ArticleRouteState.InhDetail(id, sourceId)
    is ArticleTabCollectionDetail -> ArticleRouteState.CollDetail(id)
    is ArticleTabTopicDetail -> ArticleRouteState.TopicDetail(type, key)
    else -> ArticleRouteState.List
}

private fun ArticleRouteState.toRouteKey(): Any = when (this) {
    is ArticleRouteState.List -> ArticlesList
    is ArticleRouteState.Detail -> ArticleDetail(
        id = id, sourceId = sourceId, sourceUrl = sourceUrl,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
    )
    is ArticleRouteState.DirDetail -> ArticleTabDirectoryDetail(
        id = id, sourceId = sourceId,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName == kind } ?: DirectoryItemKind.NationalProject,
    )
    is ArticleRouteState.InhDetail -> ArticleTabInheritorDetail(id = id, sourceId = sourceId)
    is ArticleRouteState.CollDetail -> ArticleTabCollectionDetail(id = id)
    is ArticleRouteState.TopicDetail -> ArticleTabTopicDetail(type = type, key = key)
}

private fun serializeArticles(stack: List<Any>): String =
    try {
        com.duckylife.heritage.modern.core.network.HeritageJson.encodeToString(stack.map { it.toArticleRouteState() })
    } catch (_: Exception) { "[]" }

private fun deserializeArticles(str: String): List<Any> =
    try {
        if (str.isBlank()) listOf(ArticlesList)
        else {
            val states = com.duckylife.heritage.modern.core.network.HeritageJson.decodeFromString<List<ArticleRouteState>>(str)
            if (states.isEmpty()) listOf(ArticlesList) else states.map { it.toRouteKey() }
        }
    } catch (_: Exception) { listOf(ArticlesList) }

// Context 目标导航 mapper
private val articleContextMapper = DetailContextRouteMapper<Any>(
    article = { ArticleDetail(id = it) },
    directoryItem = { ArticleTabDirectoryDetail(id = it) },
    inheritor = { ArticleTabInheritorDetail(id = it) },
    collection = { ArticleTabCollectionDetail(id = it) },
    topic = { type, key -> ArticleTabTopicDetail(type = type, key = key) },
)

private fun navigateContextTarget(
    target: DetailContextTarget,
    backStack: MutableList<Any>,
) {
    backStack.add(articleContextMapper.map(target))
}

@Composable
fun ArticlesNavHost(
    onSettingsSelected: () -> Unit,
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    onKeywordSearch: (String) -> Unit = {},
    onGraphExploreSelected: (contentType: String, contentId: String, initialTabName: String) -> Unit = { _, _, _ -> },
    onLearningRoutesSelected: (seedType: String?, seedId: String?) -> Unit = { _, _ -> },
    pendingNavigation: MyPageDestination.Article? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("") }
    val backStack = remember { mutableStateListOf<Any>().also { it.addAll(deserializeArticles(savedStack)) } }
    LaunchedEffect(backStack.toList()) {
        savedStack = serializeArticles(backStack.toList())
    }
    val popBackStack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
    val isInDetail = backStack.lastOrNull() !is ArticlesList
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }
    LaunchedEffect(pendingNavigation) {
        val dest = pendingNavigation ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(ArticlesList)
        backStack.add(
            ArticleDetail(
                id = dest.articleId,
                sourceId = dest.sourceId,
                sourceUrl = dest.sourceUrl,
                category = dest.category,
            ),
        )
        onPendingNavigationConsumed()
    }
    NavDisplay(
        backStack = backStack,
        onBack = popBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider@{ entryKey ->
            val key = entryKey
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            when (key) {
                is ArticlesList -> NavEntry(entryKey) {
                    ArticlesRoute(
                        onSettingsSelected = onSettingsSelected,
                        onArticleSelected = { articleId ->
                            backStack.add(ArticleDetail(id = articleId))
                        },
                        modifier = modifier,
                    )
                }

                is ArticleDetail -> NavEntry(entryKey) {
                    ArticleDetailRoute(
                        articleId = key.id,
                        sourceId = key.sourceId,
                        sourceUrl = key.sourceUrl,
                        category = key.category,
                        onBack = popBackStack,
                        onRelatedArticleSelected = { reference, category ->
                            reference.toArticleDetail(category)?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("article", it, "neighbors") }
                        },
                        onSimilarClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("article", it, "similar") }
                        },
                        onLearningRoutesClick = {
                            key.idOrSourceId?.let { onLearningRoutesSelected("content", "article:$it") }
                        },
                        onKeywordSearch = onKeywordSearch,
                        modifier = modifier,
                    )
                }

                is ArticleTabDirectoryDetail -> NavEntry(entryKey) {
                    DirectoryDetailRoute(
                        itemId = key.id,
                        sourceId = key.sourceId,
                        kind = key.kind,
                        onBack = popBackStack,
                        onRelatedProjectSelected = { reference, kind ->
                            reference.toDirectoryDetail(kind)?.let(backStack::add)
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toArticleTabInheritorDetail()?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("directoryItem", it, "neighbors") }
                        },
                        onSimilarClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("directoryItem", it, "similar") }
                        },
                        onLearningRoutesClick = {
                            key.idOrSourceId?.let { onLearningRoutesSelected("content", "directoryItem:$it") }
                        },
                        onKeywordSearch = onKeywordSearch,
                        modifier = modifier,
                    )
                }

                is ArticleTabInheritorDetail -> NavEntry(entryKey) {
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = popBackStack,
                        onRelatedProjectSelected = { reference ->
                            reference.toDirectoryDetail()?.let(backStack::add)
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toArticleTabInheritorDetail()?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("inheritor", it, "neighbors") }
                        },
                        onSimilarClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("inheritor", it, "similar") }
                        },
                        onLearningRoutesClick = {
                            key.idOrSourceId?.let { onLearningRoutesSelected("content", "inheritor:$it") }
                        },
                        onKeywordSearch = onKeywordSearch,
                        modifier = modifier,
                    )
                }

                is ArticleTabCollectionDetail -> NavEntry(entryKey) {
                    CollectionRoute(
                        id = key.id,
                        type = null,
                        topicKey = null,
                        onBack = popBackStack,
                        onArticleSelected = { id ->
                            backStack.add(ArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(ArticleTabDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(ArticleTabInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                is ArticleTabTopicDetail -> NavEntry(entryKey) {
                    ExploreTopicRoute(
                        type = key.type,
                        key = key.key,
                        onBack = popBackStack,
                        onArticleSelected = { id ->
                            backStack.add(ArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(ArticleTabDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(ArticleTabInheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(ArticleTabTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    ArticlesRoute(
                        onSettingsSelected = onSettingsSelected,
                        onArticleSelected = { articleId ->
                            backStack.add(ArticleDetail(id = articleId))
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}

private fun ArticleReferenceDto.toArticleDetail(category: ArticleCategory): ArticleDetail? =
    when {
        !sourceId.isNullOrBlank() -> ArticleDetail(
            sourceId = sourceId,
            category = category,
        )

        !detailUrl.isNullOrBlank() -> ArticleDetail(
            sourceUrl = detailUrl,
            category = category,
        )

        else -> null
    }

private fun com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto.toDirectoryDetail(
    fallbackKind: DirectoryItemKind = DirectoryItemKind.NationalProject,
): ArticleTabDirectoryDetail? {
    if (sourceId.isNullOrBlank()) return null
    return ArticleTabDirectoryDetail(
        sourceId = sourceId,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName.equals(kind, ignoreCase = true) } ?: fallbackKind,
    )
}

private fun com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto.toArticleTabInheritorDetail(): ArticleTabInheritorDetail? {
    if (sourceId.isNullOrBlank()) return null
    return ArticleTabInheritorDetail(sourceId = sourceId)
}

private fun com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto.toDirectoryItemKindOrNull(): DirectoryItemKind? =
    DirectoryItemKind.entries.firstOrNull { it.wireName.equals(kind, ignoreCase = true) }
