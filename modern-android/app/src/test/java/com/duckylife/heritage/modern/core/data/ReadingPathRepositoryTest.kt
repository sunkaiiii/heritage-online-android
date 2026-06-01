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
            source = "related",
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
        repo.record(ReadingPathEvent(id = "evt2", toType = "directoryItem", toId = "d1", source = "blendedRecommendation", createdAt = 2000L))

        repo.clear()

        val path = repo.observeRecentPath().first()
        assertTrue(path.isEmpty())
    }

    @Test
    fun `observeRecentPath returns events in reverse chronological order`() = runTest {
        val dao = FakeReadingPathDao()
        val repo = DefaultReadingPathRepository(dao)

        repo.record(ReadingPathEvent(id = "evt1", toType = "article", toId = "a1", source = "list", createdAt = 1000L))
        repo.record(ReadingPathEvent(id = "evt2", toType = "directoryItem", toId = "d1", source = "blendedRecommendation", createdAt = 2000L))

        val path = repo.observeRecentPath().first()
        assertEquals(2, path.size)
        // DAO returns in createdAt DESC order
        assertEquals("evt2", path.first().id)
        assertEquals("evt1", path.last().id)
    }

    @Test
    fun `record preserves metadata fields`() = runTest {
        val dao = FakeReadingPathDao()
        val repo = DefaultReadingPathRepository(dao)

        val event = ReadingPathEvent(
            id = "evt1",
            fromType = "directoryItem",
            fromId = "d1",
            fromTitle = "Source Directory",
            toType = "article",
            toId = "a1",
            toTitle = "Target Article",
            source = "blendedRecommendation",
            toCategory = "news",
            toKind = null,
            toSourceId = "src1",
            toSourceUrl = "https://example.test/article",
            toSubtitle = "Article subtitle",
            toImageUrl = "https://example.test/image.jpg",
            createdAt = 1000L,
        )
        repo.record(event)

        val path = repo.observeRecentPath().first()
        assertEquals(1, path.size)
        val saved = path.first()
        assertEquals("directoryItem", saved.fromType)
        assertEquals("d1", saved.fromId)
        assertEquals("Source Directory", saved.fromTitle)
        assertEquals("article", saved.toType)
        assertEquals("a1", saved.toId)
        assertEquals("Target Article", saved.toTitle)
        assertEquals("blendedRecommendation", saved.source)
        assertEquals("news", saved.toCategory)
        assertEquals("src1", saved.toSourceId)
        assertEquals("https://example.test/article", saved.toSourceUrl)
        assertEquals("Article subtitle", saved.toSubtitle)
        assertEquals("https://example.test/image.jpg", saved.toImageUrl)
    }

    @Test
    fun `upsert replaces existing event with same id`() = runTest {
        val dao = FakeReadingPathDao()
        val repo = DefaultReadingPathRepository(dao)

        repo.record(ReadingPathEvent(id = "evt1", toType = "article", toId = "a1", source = "list", createdAt = 1000L))
        repo.record(ReadingPathEvent(id = "evt1", toType = "directoryItem", toId = "d1", source = "blended", createdAt = 2000L))

        val path = repo.observeRecentPath().first()
        assertEquals(1, path.size)
        assertEquals("directoryItem", path.first().toType)
    }
}
