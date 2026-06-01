package com.duckylife.heritage.modern.feature.my

import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

/**
 * 将阅读路径事件转换为我的页导航目标。
 *
 * 回跳优先级：
 * - 如果有 toSourceId 或 toSourceUrl，说明来源是 related reference，此时优先用 sourceId/sourceUrl 路由，
 *   不把 toId（可能是 sourceId）当作详情主键。
 * - 否则使用 toId 作为详情主键。
 */
internal fun ReadingPathEvent.toMyPageDestination(): MyPageDestination? =
    when (toType) {
        "article" -> {
            val hasSource = !toSourceId.isNullOrBlank() || !toSourceUrl.isNullOrBlank()
            MyPageDestination.Article(
                articleId = if (hasSource) null else toId,
                sourceId = toSourceId,
                sourceUrl = toSourceUrl,
                category = ArticleCategory.entries.firstOrNull { it.wireName == toCategory }
                    ?: ArticleCategory.News,
            )
        }
        "directoryItem" -> {
            MyPageDestination.Directory(
                itemId = if (!toSourceId.isNullOrBlank()) null else toId,
                sourceId = toSourceId,
                kind = DirectoryItemKind.entries.firstOrNull { it.wireName == toKind }
                    ?: DirectoryItemKind.NationalProject,
            )
        }
        "inheritor" -> {
            MyPageDestination.Inheritor(
                inheritorId = if (!toSourceId.isNullOrBlank()) null else toId,
                sourceId = toSourceId,
            )
        }
        else -> null
    }
