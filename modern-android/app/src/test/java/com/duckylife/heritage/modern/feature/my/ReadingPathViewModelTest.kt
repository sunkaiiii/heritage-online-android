package com.duckylife.heritage.modern.feature.my

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
class ReadingPathViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeReadingPathRepository : ReadingPathRepository {
        private val events = MutableStateFlow<List<ReadingPathEvent>>(emptyList())

        override fun observeRecentPath(limit: Int): Flow<List<ReadingPathEvent>> = events

        override suspend fun record(event: ReadingPathEvent) {
            events.value = events.value + event
        }

        override suspend fun clear() {
            events.value = emptyList()
        }
    }

    @Test
    fun `initial state is empty`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathViewModel(readingPathRepository = repo)

        advanceUntilIdle()

        val events = viewModel.events.first()
        assertTrue(events.isEmpty())
    }

    @Test
    fun `events are reflected after recording`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathViewModel(readingPathRepository = repo)

        repo.record(ReadingPathEvent(
            id = "evt1",
            toType = "article",
            toId = "a1",
            toTitle = "Test Article",
            source = "list",
            createdAt = 1000L,
        ))
        advanceUntilIdle()

        val events = viewModel.events.first()
        assertEquals(1, events.size)
        assertEquals("evt1", events.first().id)
    }

    @Test
    fun `clearAll empties the list`() = runTest {
        val repo = FakeReadingPathRepository()
        val viewModel = ReadingPathViewModel(readingPathRepository = repo)

        repo.record(ReadingPathEvent(
            id = "evt1",
            toType = "article",
            toId = "a1",
            source = "list",
            createdAt = 1000L,
        ))
        advanceUntilIdle()
        assertEquals(1, viewModel.events.first().size)

        viewModel.clearAll()
        advanceUntilIdle()
        assertTrue(viewModel.events.first().isEmpty())
    }
}
