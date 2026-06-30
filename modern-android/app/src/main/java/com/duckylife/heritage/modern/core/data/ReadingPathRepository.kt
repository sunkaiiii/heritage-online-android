package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.database.dao.ReadingPathDao
import com.duckylife.heritage.modern.core.database.entity.ReadingPathEventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * 阅读路径事件的领域模型。
 */
data class ReadingPathEvent(
    val id: String = UUID.randomUUID().toString(),
    val fromType: String? = null,
    val fromId: String? = null,
    val fromTitle: String? = null,
    val toType: String,
    val toId: String,
    val toTitle: String? = null,
    val source: String,
    val toCategory: String? = null,
    val toKind: String? = null,
    val toSourceId: String? = null,
    val toSourceUrl: String? = null,
    val toSubtitle: String? = null,
    val toImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * 阅读路径内容引用，用于构造 from/to。
 */
data class ReadingPathContentRef(
    val type: String,
    val id: String,
    val title: String,
    val category: String? = null,
    val kind: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val subtitle: String? = null,
    val imageUrl: String? = null,
)

/**
 * 阅读路径仓库接口。
 */
interface ReadingPathRepository {
    fun observeRecentPath(limit: Int = 50): Flow<List<ReadingPathEvent>>
    suspend fun record(event: ReadingPathEvent)
    suspend fun clear()
}

/**
 * 基于 Room 的阅读路径仓库实现。
 */
class DefaultReadingPathRepository @Inject constructor(
    private val dao: ReadingPathDao,
) : ReadingPathRepository {

    override fun observeRecentPath(limit: Int): Flow<List<ReadingPathEvent>> =
        dao.observeRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun record(event: ReadingPathEvent) {
        dao.upsert(event.toEntity())
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}

private fun ReadingPathEventEntity.toDomain(): ReadingPathEvent = ReadingPathEvent(
    id = id,
    fromType = fromType,
    fromId = fromId,
    fromTitle = fromTitle,
    toType = toType,
    toId = toId,
    toTitle = toTitle,
    source = source,
    toCategory = toCategory,
    toKind = toKind,
    toSourceId = toSourceId,
    toSourceUrl = toSourceUrl,
    toSubtitle = toSubtitle,
    toImageUrl = toImageUrl,
    createdAt = createdAt,
)

private fun ReadingPathEvent.toEntity(): ReadingPathEventEntity = ReadingPathEventEntity(
    id = id.ifBlank { UUID.randomUUID().toString() },
    fromType = fromType,
    fromId = fromId,
    fromTitle = fromTitle,
    toType = toType,
    toId = toId,
    toTitle = toTitle,
    source = source,
    toCategory = toCategory,
    toKind = toKind,
    toSourceId = toSourceId,
    toSourceUrl = toSourceUrl,
    toSubtitle = toSubtitle,
    toImageUrl = toImageUrl,
    createdAt = createdAt,
)
