package com.duckylife.heritage.modern.feature.timeline

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.TimelineItemDto
import com.duckylife.heritage.modern.core.network.dto.TimelineV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.ui.error.ErrorKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimelineViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region empty data state

    @Test
    fun `empty years list shows empty state without error`() = runTest {
        val repo = FakeHeritageRepository(timelineYearsList = emptyList())
        val viewModel = TimelineViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.years.isEmpty())
        assertNull(state.selectedYear)
        assertNull(state.errorKind)
    }

    @Test
    fun `empty items for selected year shows empty state`() = runTest {
        val repo = FakeHeritageRepository(
            timelineYearsList = listOf(TimelineYearBucketDto(year = 2020)),
            timelineV2Response = TimelineV2ResponseDto(
                items = emptyList(),
                total = 0,
                hasMore = false,
            ),
        )
        val viewModel = TimelineViewModel(repository = repo)
        advanceUntilIdle()

        viewModel.selectYear(2020)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2020, state.selectedYear)
        assertTrue(state.items.isEmpty())
        assertFalse(state.hasMore)
        assertNull(state.errorKind)
    }

    @Test
    fun `years load failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("network down"))
        val viewModel = TimelineViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `items load failure sets errorKind`() = runTest {
        val repo = SlowTimelineRepository(delayMs = 0, failItems = true)
        val viewModel = TimelineViewModel(repository = repo)
        advanceUntilIdle()

        viewModel.selectYear(2020)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    // endregion

    // region fast state switching

    @Test
    fun `fast year switch cancels previous load`() = runTest {
        val repo = SlowTimelineRepository(delayMs = 100)
        val viewModel = TimelineViewModel(repository = repo)
        advanceUntilIdle()

        viewModel.selectYear(2020)
        advanceTimeBy(50)

        viewModel.selectYear(2021)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertEquals(2021, state.selectedYear)
        assertEquals(2021, repo.lastYear)
    }

    @Test
    fun `fast type toggle cancels previous load`() = runTest {
        val repo = SlowTimelineRepository(delayMs = 100)
        val viewModel = TimelineViewModel(repository = repo)
        advanceUntilIdle()

        viewModel.selectYear(2020)
        advanceTimeBy(50)

        viewModel.toggleType(SearchResultType.Article)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertTrue(SearchResultType.Article in state.selectedTypes)
    }

    // endregion
}

private class SlowTimelineRepository(
    private val delayMs: Long,
    private val failItems: Boolean = false,
    internal val delegate: FakeHeritageRepository = FakeHeritageRepository(
        timelineYearsList = listOf(
            TimelineYearBucketDto(year = 2020, total = 5),
            TimelineYearBucketDto(year = 2021, total = 3),
        ),
        timelineV2Response = TimelineV2ResponseDto(
            items = listOf(
                TimelineItemDto(id = "1", type = "article", title = "Item 1"),
            ),
            total = 1,
            hasMore = false,
        ),
    ),
) : HeritageRepository by delegate {
    var lastYear: Int? = null
        private set

    override suspend fun timelineYears(): List<TimelineYearBucketDto> {
        delay(delayMs)
        return delegate.timelineYears()
    }

    override suspend fun timelineV2(query: com.duckylife.heritage.modern.core.network.TimelineV2Query): TimelineV2ResponseDto {
        delay(delayMs)
        lastYear = query.year
        if (failItems) throw IllegalStateException("items load failed")
        return delegate.timelineV2(query)
    }
}
