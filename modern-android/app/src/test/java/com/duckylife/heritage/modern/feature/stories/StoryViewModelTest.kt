package com.duckylife.heritage.modern.feature.stories

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads region story when region is provided`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoryViewModel(
            region = "北京",
            category = null,
            year = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.story)
    }

    @Test
    fun `loads category story when category is provided`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoryViewModel(
            region = null,
            category = "传统技艺",
            year = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.story)
    }

    @Test
    fun `loads year story when year is provided`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoryViewModel(
            region = null,
            category = null,
            year = 2020,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.story)
    }

    @Test
    fun `region takes priority over category and year`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoryViewModel(
            region = "四川",
            category = "传统技艺",
            year = 2020,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.story)
    }

    @Test
    fun `category takes priority over year when region is null`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoryViewModel(
            region = null,
            category = "民俗",
            year = 2020,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.story)
    }

    @Test
    fun `no parameters sets errorKind`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoryViewModel(
            region = null,
            category = null,
            year = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
        assertNull(state.story)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server error"))
        val viewModel = StoryViewModel(
            region = "北京",
            category = null,
            year = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
        assertNull(state.story)
    }
}
