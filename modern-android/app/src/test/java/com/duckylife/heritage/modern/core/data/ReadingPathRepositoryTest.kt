package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.database.dao.ReadingPathDao
import com.duckylife.heritage.modern.core.database.entity.ReadingPathEventEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingPathRepositoryTest {

    private class FakeReadingPathDao : ReadingPathDao {
        private val events = mutableListOf<ReadingPathEventEntity>()
        var observeLimit: Int? = null

        override fun observeRecent(limit: Int) = flowOf(events.sortedByDescending { it.createdAt }.take(limit))

        override suspend fun upsert(event: ReadingPathEventEntity) {
            events.removeAll { it.id == event.id }
            events.add(event)
        }

        override suspend fun deleteAll() {
            events.clear()
        }
    }

    @Test
    fun `record saves event`() = runTest {
        val dao = FakeReadingPathDao()
        val repo = DefaultReadingPathRepository(dao)

        val event = ReadingPathEvent(
            id = "evt1",
            toType = "article",
            toId = "a1",
            toTitle = "Test Article",
            source = "context_related",
            createdAt = 1000L,
        )
        repo.record(event)

        val path = repo.observeRecentPath().first()
        assertEquals(1, path.size)
        assertEquals("evt1", path.first().id)
        assertEquals("article", path.first().toType)
    }

    @Test
    fun `clear removes all events`() = runTest {
        val dao = FakeReadingPathDao()
        val repo = DefaultReadingPathRepository(dao)

        repo.record(ReadingPathEvent(id = "evt1", toType = "article", toId = "a1", source = "list", createdAt = 1000L))
        repo.record(ReadingPathEvent(id = "evt2", toType = "directoryItem", toId = "d1", source = "blended", createdAt = 2000L))

        repo.clear()

        val path = repo.observeRecentPath().first()
        assertTrue(path.isEmpty())
    }

    @Test
    fun `observeRecentPath returns events in reverse chronological order`() = runTest {
        val dao = FakeReadingPathDao()
        val repo = DefaultReadingPathRepository(dao)

        repo.record(ReadingPathEvent(id = "evt1", toType = "article", toId = "a1", source = "list", createdAt = 1000L))
        repo.record(ReadingPathEvent(id = "evt2", toType = "directoryItem", toId = "d1", source = "blended", createdAt = 2000L))

        val path = repo.observeRecentPath().first()
        assertEquals(2, path.size)
        // DAO returns in createdAt DESC order
        assertEquals("evt2", path.first().id)
        assertEquals("evt1", path.last().id)
    }
}
