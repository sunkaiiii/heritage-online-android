package com.duckylife.heritage.modern.core.database.dao

import com.duckylife.heritage.modern.core.database.entity.ReadingPathEventEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ReadingPathDao 的单元测试。
 *
 * 由于 Room DAO 需要真实的数据库实例，这里使用 Fake 实现来验证
 * DAO 接口的契约行为。实际的 Room DAO 集成测试在 androidTest 中。
 */
class ReadingPathDaoTest {

    private class FakeReadingPathDao : ReadingPathDao {
        private val events = mutableListOf<ReadingPathEventEntity>()

        override fun observeRecent(limit: Int) =
            kotlinx.coroutines.flow.flowOf(events.sortedByDescending { it.createdAt }.take(limit))

        override suspend fun upsert(event: ReadingPathEventEntity) {
            events.removeAll { it.id == event.id }
            events.add(event)
        }

        override suspend fun deleteAll() {
            events.clear()
        }
    }

    @Test
    fun `upsert inserts event`() = runTest {
        val dao = FakeReadingPathDao()
        val event = ReadingPathEventEntity(
            id = "evt1",
            toType = "article",
            toId = "a1",
            source = "list",
            createdAt = 1000L,
        )

        dao.upsert(event)

        val result = dao.observeRecent().first()
        assertEquals(1, result.size)
        assertEquals("evt1", result.first().id)
    }

    @Test
    fun `upsert replaces existing event with same id`() = runTest {
        val dao = FakeReadingPathDao()
        val event1 = ReadingPathEventEntity(
            id = "evt1",
            toType = "article",
            toId = "a1",
            source = "list",
            createdAt = 1000L,
        )
        val event2 = ReadingPathEventEntity(
            id = "evt1",
            toType = "directoryItem",
            toId = "d1",
            source = "blended",
            createdAt = 2000L,
        )

        dao.upsert(event1)
        dao.upsert(event2)

        val result = dao.observeRecent().first()
        assertEquals(1, result.size)
        assertEquals("directoryItem", result.first().toType)
    }

    @Test
    fun `deleteAll removes all events`() = runTest {
        val dao = FakeReadingPathDao()
        dao.upsert(ReadingPathEventEntity(id = "evt1", toType = "article", toId = "a1", source = "list", createdAt = 1000L))
        dao.upsert(ReadingPathEventEntity(id = "evt2", toType = "directoryItem", toId = "d1", source = "blended", createdAt = 2000L))

        dao.deleteAll()

        val result = dao.observeRecent().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `observeRecent returns events in descending order`() = runTest {
        val dao = FakeReadingPathDao()
        dao.upsert(ReadingPathEventEntity(id = "evt1", toType = "article", toId = "a1", source = "list", createdAt = 1000L))
        dao.upsert(ReadingPathEventEntity(id = "evt2", toType = "directoryItem", toId = "d1", source = "blended", createdAt = 2000L))

        val result = dao.observeRecent().first()
        assertEquals(2, result.size)
        assertEquals("evt2", result[0].id)
        assertEquals("evt1", result[1].id)
    }

    @Test
    fun `observeRecent respects limit`() = runTest {
        val dao = FakeReadingPathDao()
        for (i in 1..5) {
            dao.upsert(ReadingPathEventEntity(id = "evt$i", toType = "article", toId = "a$i", source = "list", createdAt = i * 1000L))
        }

        val result = dao.observeRecent(limit = 3).first()
        assertEquals(3, result.size)
    }
}
