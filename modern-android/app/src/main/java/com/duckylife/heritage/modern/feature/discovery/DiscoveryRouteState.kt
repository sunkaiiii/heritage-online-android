package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

// ---------------------------------------------------------------------------
// Route keys — typed sealed interface replacing List<Any>
// ---------------------------------------------------------------------------

internal sealed interface DiscoveryRouteKey {
    data object DiscoveryIndex : DiscoveryRouteKey
    data class SearchResults(val query: String) : DiscoveryRouteKey
    data class ExploreTopicDetail(val type: String, val key: String) : DiscoveryRouteKey
    data class LearningPathDetail(val id: String) : DiscoveryRouteKey
    data class CollectionDetail(val id: String? = null, val type: String? = null, val key: String? = null) : DiscoveryRouteKey
    data object RegionAtlasPage : DiscoveryRouteKey
    data class RegionDetailPage(val region: String) : DiscoveryRouteKey
    data object TimelinePage : DiscoveryRouteKey
    data object TaxonomyPage : DiscoveryRouteKey
    data class TaxonomyDetailPage(val type: String, val key: String) : DiscoveryRouteKey
    data class ComparePage(val type: String? = null, val left: String? = null, val right: String? = null) : DiscoveryRouteKey
    data object StoriesIndexPage : DiscoveryRouteKey
    data class StoryPage(val region: String? = null, val category: String? = null, val year: Int? = null) : DiscoveryRouteKey
    data class DeepDivePage(val seedType: String, val seedId: String) : DiscoveryRouteKey
    data class DiscoveryArticleDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val sourceUrl: String? = null,
        val category: ArticleCategory = ArticleCategory.News,
    ) : DiscoveryRouteKey

    data class DiscoveryDirectoryDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ) : DiscoveryRouteKey

    data class DiscoveryInheritorDetail(
        val id: String? = null,
        val sourceId: String? = null,
    ) : DiscoveryRouteKey
}

// ---------------------------------------------------------------------------
// Serializable wire format
// ---------------------------------------------------------------------------

@kotlinx.serialization.Serializable
internal sealed interface RouteState {
    // Discriminator field is "type" — avoid using "type" as a field name in subtypes
    @kotlinx.serialization.Serializable data object Index : RouteState
    @kotlinx.serialization.Serializable data class Search(val query: String = "") : RouteState
    @kotlinx.serialization.Serializable data class ExploreTopic(@kotlinx.serialization.SerialName("topicType") val type: String = "", val key: String = "") : RouteState
    @kotlinx.serialization.Serializable data class LearningPath(val id: String = "") : RouteState
    @kotlinx.serialization.Serializable data class CollectionState(val id: String? = null, @kotlinx.serialization.SerialName("topicType") val type: String? = null, val key: String? = null) : RouteState
    @kotlinx.serialization.Serializable data object RegionAtlas : RouteState
    @kotlinx.serialization.Serializable data class RegionDetail(val region: String = "") : RouteState
    @kotlinx.serialization.Serializable data object Timeline : RouteState
    @kotlinx.serialization.Serializable data object Taxonomy : RouteState
    @kotlinx.serialization.Serializable data class TaxonomyDetail(@kotlinx.serialization.SerialName("topicType") val type: String = "", val key: String = "") : RouteState
    @kotlinx.serialization.Serializable data class Compare(@kotlinx.serialization.SerialName("topicType") val type: String? = null, val left: String? = null, val right: String? = null) : RouteState
    @kotlinx.serialization.Serializable data object StoriesIndex : RouteState
    @kotlinx.serialization.Serializable data class Story(val region: String? = null, val category: String? = null, val year: Int? = null) : RouteState
    @kotlinx.serialization.Serializable data class DeepDive(val seedType: String = "", val seedId: String = "") : RouteState
    @kotlinx.serialization.Serializable data class ArticleDetail(val id: String? = null, val sourceId: String? = null, val sourceUrl: String? = null, val category: String = "news") : RouteState
    @kotlinx.serialization.Serializable data class DirectoryDetail(val id: String? = null, val sourceId: String? = null, val kind: String = "nationalProject") : RouteState
    @kotlinx.serialization.Serializable data class InheritorDetail(val id: String? = null, val sourceId: String? = null) : RouteState
}

// ---------------------------------------------------------------------------
// Mapping: RouteKey <-> RouteState
// ---------------------------------------------------------------------------

internal fun DiscoveryRouteKey.toRouteState(): RouteState = when (this) {
    is DiscoveryRouteKey.DiscoveryIndex -> RouteState.Index
    is DiscoveryRouteKey.SearchResults -> RouteState.Search(query)
    is DiscoveryRouteKey.ExploreTopicDetail -> RouteState.ExploreTopic(type, key)
    is DiscoveryRouteKey.LearningPathDetail -> RouteState.LearningPath(id)
    is DiscoveryRouteKey.CollectionDetail -> RouteState.CollectionState(id, type, key)
    is DiscoveryRouteKey.RegionAtlasPage -> RouteState.RegionAtlas
    is DiscoveryRouteKey.RegionDetailPage -> RouteState.RegionDetail(region)
    is DiscoveryRouteKey.TimelinePage -> RouteState.Timeline
    is DiscoveryRouteKey.TaxonomyPage -> RouteState.Taxonomy
    is DiscoveryRouteKey.TaxonomyDetailPage -> RouteState.TaxonomyDetail(type, key)
    is DiscoveryRouteKey.ComparePage -> RouteState.Compare(type, left, right)
    is DiscoveryRouteKey.StoriesIndexPage -> RouteState.StoriesIndex
    is DiscoveryRouteKey.StoryPage -> RouteState.Story(region, category, year)
    is DiscoveryRouteKey.DeepDivePage -> RouteState.DeepDive(seedType, seedId)
    is DiscoveryRouteKey.DiscoveryArticleDetail -> RouteState.ArticleDetail(id, sourceId, sourceUrl, category.wireName)
    is DiscoveryRouteKey.DiscoveryDirectoryDetail -> RouteState.DirectoryDetail(id, sourceId, kind.wireName)
    is DiscoveryRouteKey.DiscoveryInheritorDetail -> RouteState.InheritorDetail(id, sourceId)
}

internal fun RouteState.toRouteKey(): DiscoveryRouteKey = when (this) {
    is RouteState.Index -> DiscoveryRouteKey.DiscoveryIndex
    is RouteState.Search -> DiscoveryRouteKey.SearchResults(query)
    is RouteState.ExploreTopic -> DiscoveryRouteKey.ExploreTopicDetail(type, key)
    is RouteState.LearningPath -> DiscoveryRouteKey.LearningPathDetail(id)
    is RouteState.CollectionState -> DiscoveryRouteKey.CollectionDetail(id, type, key)
    is RouteState.RegionAtlas -> DiscoveryRouteKey.RegionAtlasPage
    is RouteState.RegionDetail -> DiscoveryRouteKey.RegionDetailPage(region)
    is RouteState.Timeline -> DiscoveryRouteKey.TimelinePage
    is RouteState.Taxonomy -> DiscoveryRouteKey.TaxonomyPage
    is RouteState.TaxonomyDetail -> DiscoveryRouteKey.TaxonomyDetailPage(type, key)
    is RouteState.Compare -> DiscoveryRouteKey.ComparePage(type, left, right)
    is RouteState.StoriesIndex -> DiscoveryRouteKey.StoriesIndexPage
    is RouteState.Story -> DiscoveryRouteKey.StoryPage(region, category, year)
    is RouteState.DeepDive -> DiscoveryRouteKey.DeepDivePage(seedType, seedId)
    is RouteState.ArticleDetail -> DiscoveryRouteKey.DiscoveryArticleDetail(
        id = id, sourceId = sourceId, sourceUrl = sourceUrl,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
    )
    is RouteState.DirectoryDetail -> DiscoveryRouteKey.DiscoveryDirectoryDetail(
        id = id, sourceId = sourceId,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName == kind } ?: DirectoryItemKind.NationalProject,
    )
    is RouteState.InheritorDetail -> DiscoveryRouteKey.DiscoveryInheritorDetail(id = id, sourceId = sourceId)
}
