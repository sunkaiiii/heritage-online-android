package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 最近阅读内容的领域引用。
 */
data class RecentContentRef(
    val type: SearchResultType,
    val id: String,
    val title: String?,
)

/**
 * 从本地阅读路径中提供最一条可导航的内容。
 */
interface RecentContentProvider {
    fun observeRecentContent(): Flow<RecentContentRef?>
}

class DefaultRecentContentProvider @Inject constructor(
    private val readingPathRepository: ReadingPathRepository,
) : RecentContentProvider {

    override fun observeRecentContent(): Flow<RecentContentRef?> =
        readingPathRepository.observeRecentPath(limit = 1).map { events ->
            events.firstNotNullOfOrNull { event ->
                val type = SearchResultType.fromWireName(event.toType)
                val id = event.toId.takeIf { it.isNotBlank() }
                if (type != null && id != null) {
                    RecentContentRef(type = type, id = id, title = event.toTitle)
                } else {
                    null
                }
            }
        }
}
