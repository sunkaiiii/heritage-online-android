package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

// ---------------------------------------------------------------------------
// 路由 key：用强类型替代 List<Any> 的临时字符串状态
// ---------------------------------------------------------------------------

internal sealed interface DirectoryRouteKey {
    data object DirectoryList : DirectoryRouteKey

    data class DirectoryDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ) : DirectoryRouteKey

    data class DirectoryInheritorDetail(
        val id: String? = null,
        val sourceId: String? = null,
    ) : DirectoryRouteKey

    // 跨内容类型路由（从 Context 点击进入）
    data class DirectoryTabArticleDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val sourceUrl: String? = null,
        val category: ArticleCategory = ArticleCategory.News,
    ) : DirectoryRouteKey

    data class DirectoryTabCollectionDetail(val id: String) : DirectoryRouteKey

    data class DirectoryTabTopicDetail(val type: String, val key: String) : DirectoryRouteKey
}

// ---------------------------------------------------------------------------
// 可持久化的路由状态
// ---------------------------------------------------------------------------

@kotlinx.serialization.Serializable
internal sealed interface DirectoryRouteState {
    @kotlinx.serialization.Serializable data object List : DirectoryRouteState
    @kotlinx.serialization.Serializable data class Detail(
        val id: String? = null,
        val sourceId: String? = null,
        val kind: String = "nationalProject",
    ) : DirectoryRouteState

    @kotlinx.serialization.Serializable data class InheritorDetail(
        val id: String? = null,
        val sourceId: String? = null,
    ) : DirectoryRouteState

    @kotlinx.serialization.Serializable data class ArticleDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val sourceUrl: String? = null,
        val category: String = "news",
    ) : DirectoryRouteState

    @kotlinx.serialization.Serializable data class CollectionDetail(val id: String = "") : DirectoryRouteState

    @kotlinx.serialization.Serializable data class TopicDetail(
        @kotlinx.serialization.SerialName("topicType") val type: String = "",
        val key: String = "",
    ) : DirectoryRouteState
}

// ---------------------------------------------------------------------------
// 路由 key 与可持久化状态之间的转换
// ---------------------------------------------------------------------------

internal fun DirectoryRouteKey.toRouteState(): DirectoryRouteState = when (this) {
    is DirectoryRouteKey.DirectoryList -> DirectoryRouteState.List
    is DirectoryRouteKey.DirectoryDetail -> DirectoryRouteState.Detail(id, sourceId, kind.wireName)
    is DirectoryRouteKey.DirectoryInheritorDetail -> DirectoryRouteState.InheritorDetail(id, sourceId)
    is DirectoryRouteKey.DirectoryTabArticleDetail -> DirectoryRouteState.ArticleDetail(id, sourceId, sourceUrl, category.wireName)
    is DirectoryRouteKey.DirectoryTabCollectionDetail -> DirectoryRouteState.CollectionDetail(id)
    is DirectoryRouteKey.DirectoryTabTopicDetail -> DirectoryRouteState.TopicDetail(type, key)
}

internal fun DirectoryRouteState.toRouteKey(): DirectoryRouteKey = when (this) {
    is DirectoryRouteState.List -> DirectoryRouteKey.DirectoryList
    is DirectoryRouteState.Detail -> DirectoryRouteKey.DirectoryDetail(
        id = id, sourceId = sourceId,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName == kind } ?: DirectoryItemKind.NationalProject,
    )
    is DirectoryRouteState.InheritorDetail -> DirectoryRouteKey.DirectoryInheritorDetail(id = id, sourceId = sourceId)
    is DirectoryRouteState.ArticleDetail -> DirectoryRouteKey.DirectoryTabArticleDetail(
        id = id, sourceId = sourceId, sourceUrl = sourceUrl,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
    )
    is DirectoryRouteState.CollectionDetail -> DirectoryRouteKey.DirectoryTabCollectionDetail(id = id)
    is DirectoryRouteState.TopicDetail -> DirectoryRouteKey.DirectoryTabTopicDetail(type = type, key = key)
}
