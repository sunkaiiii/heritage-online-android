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
    val id: String,
    val fromType: String? = null,
    val fromId: String? = null,
    val fromTitle: String? = null,
    val toType: String,
    val toId: String,
    val toTitle: String? = null,
    val source: String,
    val createdAt: Long,
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
    createdAt = createdAt,
)
