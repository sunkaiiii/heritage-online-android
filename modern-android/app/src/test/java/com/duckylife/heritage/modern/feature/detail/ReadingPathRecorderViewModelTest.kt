package com.duckylife.heritage.modern.feature.detail

import com.duckylife.heritage.modern.core.data.ReadingPathContentRef
import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.data.ReadingPathRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingPathRecorderViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeReadingPathRepository : ReadingPathRepository {
        private val events = MutableStateFlow<List<ReadingPathEvent>>(emptyList())
        val recordedEvents = mutableListOf<ReadingPathEvent>()

        override fun observeRecentPath(limit: Int): Flow<List<ReadingPathEvent>> = events

        override suspend fun record(event: ReadingPathEvent) {
            recordedEvents.add(event)
            events.value = events.value + event
        }

        override suspend fun clear() {
            events.value = emptyList()
            recordedEvents.clear()
        }
    }

    @Test
    fun `record saves event with metadata`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathRecorderViewModel(repository = repo)

        val from = ReadingPathContentRef(
            type = "article",
            id = "a1",
            title = "Source Article",
            category = "news",
            sourceId = "src1",
            sourceUrl = "https://example.test/article",
        )
        val to = ReadingPathContentRef(
            type = "directoryItem",
            id = "d1",
            title = "Target Directory",
            kind = "nationalProject",
            sourceId = "toSrc1",
            sourceUrl = "https://example.test/directory",
        )
        viewModel.record(from = from, to = to, source = "blendedRecommendation")
        advanceUntilIdle()

        assertEquals(1, repo.recordedEvents.size)
        val event = repo.recordedEvents.first()
        assertEquals("article", event.fromType)
        assertEquals("a1", event.fromId)
        assertEquals("Source Article", event.fromTitle)
        assertEquals("directoryItem", event.toType)
        assertEquals("d1", event.toId)
        assertEquals("Target Directory", event.toTitle)
        assertEquals("blendedRecommendation", event.source)
        assertEquals(null, event.toCategory) // to.category was null
        assertEquals("nationalProject", event.toKind)
        assertEquals("toSrc1", event.toSourceId)
        assertEquals("https://example.test/directory", event.toSourceUrl)
    }

    @Test
    fun `record generates stable id from from to source`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathRecorderViewModel(repository = repo)

        val from = ReadingPathContentRef(type = "article", id = "a1", title = "A")
        val to = ReadingPathContentRef(type = "directoryItem", id = "d1", title = "D")

        viewModel.record(from = from, to = to, source = "related")
        advanceUntilIdle()

        assertEquals("article:a1->directoryItem:d1:related", repo.recordedEvents.first().id)
    }

    @Test
    fun `record skips collection type`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathRecorderViewModel(repository = repo)

        val from = ReadingPathContentRef(type = "article", id = "a1", title = "A")
        val to = ReadingPathContentRef(type = "collection", id = "c1", title = "C")

        viewModel.record(from = from, to = to, source = "collection")
        advanceUntilIdle()

        assertTrue(repo.recordedEvents.isEmpty())
    }

    @Test
    fun `record skips topic type`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathRecorderViewModel(repository = repo)

        val from = ReadingPathContentRef(type = "article", id = "a1", title = "A")
        val to = ReadingPathContentRef(type = "topic", id = "传统技艺", title = "传统技艺")

        viewModel.record(from = from, to = to, source = "exploreTopic")
        advanceUntilIdle()

        assertTrue(repo.recordedEvents.isEmpty())
    }

    @Test
    fun `record failure does not throw`() = runTest {
        val repo = object : ReadingPathRepository {
            override fun observeRecentPath(limit: Int) = MutableStateFlow<List<ReadingPathEvent>>(emptyList())
            override suspend fun record(event: ReadingPathEvent) = throw RuntimeException("DB error")
            override suspend fun clear() {}
        }
        val viewModel = ReadingPathRecorderViewModel(repository = repo)

        val from = ReadingPathContentRef(type = "article", id = "a1", title = "A")
        val to = ReadingPathContentRef(type = "directoryItem", id = "d1", title = "D")

        // Should not throw
        viewModel.record(from = from, to = to, source = "related")
        advanceUntilIdle()
    }
}
