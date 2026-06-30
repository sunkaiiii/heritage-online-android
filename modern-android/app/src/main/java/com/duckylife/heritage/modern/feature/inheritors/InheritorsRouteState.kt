package com.duckylife.heritage.modern.feature.inheritors

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

// ---------------------------------------------------------------------------
// 路由 key：用强类型替代 List<Any> 的临时字符串状态
// ---------------------------------------------------------------------------

internal sealed interface InheritorsRouteKey {
    data object InheritorsList : InheritorsRouteKey

    data class InheritorDetail(
        val id: String? = null,
        val sourceId: String? = null,
    ) : InheritorsRouteKey

    data class InheritorDirectoryDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ) : InheritorsRouteKey

    // 跨内容类型路由（从 Context 点击进入）
    data class InheritorTabArticleDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val sourceUrl: String? = null,
        val category: ArticleCategory = ArticleCategory.News,
    ) : InheritorsRouteKey

    data class InheritorTabCollectionDetail(val id: String) : InheritorsRouteKey

    data class InheritorTabTopicDetail(val type: String, val key: String) : InheritorsRouteKey
}

// ---------------------------------------------------------------------------
// 可持久化的路由状态
// ---------------------------------------------------------------------------

@kotlinx.serialization.Serializable
internal sealed interface InheritorsRouteState {
    @kotlinx.serialization.Serializable data object List : InheritorsRouteState
    @kotlinx.serialization.Serializable data class Detail(
        val id: String? = null,
        val sourceId: String? = null,
    ) : InheritorsRouteState

    @kotlinx.serialization.Serializable data class DirectoryDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val kind: String = "nationalProject",
    ) : InheritorsRouteState

    @kotlinx.serialization.Serializable data class ArticleDetail(
        val id: String? = null,
        val sourceId: String? = null,
        val sourceUrl: String? = null,
        val category: String = "news",
    ) : InheritorsRouteState

    @kotlinx.serialization.Serializable data class CollectionDetail(val id: String = "") : InheritorsRouteState

    @kotlinx.serialization.Serializable data class TopicDetail(
        @kotlinx.serialization.SerialName("topicType") val type: String = "",
        val key: String = "",
    ) : InheritorsRouteState
}

// ---------------------------------------------------------------------------
// 路由 key 与可持久化状态之间的转换
// ---------------------------------------------------------------------------

internal fun InheritorsRouteKey.toRouteState(): InheritorsRouteState = when (this) {
    is InheritorsRouteKey.InheritorsList -> InheritorsRouteState.List
    is InheritorsRouteKey.InheritorDetail -> InheritorsRouteState.Detail(id, sourceId)
    is InheritorsRouteKey.InheritorDirectoryDetail -> InheritorsRouteState.DirectoryDetail(id, sourceId, kind.wireName)
    is InheritorsRouteKey.InheritorTabArticleDetail -> InheritorsRouteState.ArticleDetail(id, sourceId, sourceUrl, category.wireName)
    is InheritorsRouteKey.InheritorTabCollectionDetail -> InheritorsRouteState.CollectionDetail(id)
    is InheritorsRouteKey.InheritorTabTopicDetail -> InheritorsRouteState.TopicDetail(type, key)
}

internal fun InheritorsRouteState.toRouteKey(): InheritorsRouteKey = when (this) {
    is InheritorsRouteState.List -> InheritorsRouteKey.InheritorsList
    is InheritorsRouteState.Detail -> InheritorsRouteKey.InheritorDetail(id = id, sourceId = sourceId)
    is InheritorsRouteState.DirectoryDetail -> InheritorsRouteKey.InheritorDirectoryDetail(
        id = id, sourceId = sourceId,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName == kind } ?: DirectoryItemKind.NationalProject,
    )
    is InheritorsRouteState.ArticleDetail -> InheritorsRouteKey.InheritorTabArticleDetail(
        id = id, sourceId = sourceId, sourceUrl = sourceUrl,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
    )
    is InheritorsRouteState.CollectionDetail -> InheritorsRouteKey.InheritorTabCollectionDetail(id = id)
    is InheritorsRouteState.TopicDetail -> InheritorsRouteKey.InheritorTabTopicDetail(type = type, key = key)
}
