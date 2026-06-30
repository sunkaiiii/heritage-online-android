package com.duckylife.heritage.modern.feature.explore

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
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
class ExploreTopicViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `empty topic loads without error`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = ExploreTopicViewModel(
            type = "category",
            key = "传统技艺",
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.topic)
        assertNull(state.errorKind)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("not found"))
        val viewModel = ExploreTopicViewModel(
            type = "category",
            key = "missing",
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.topic)
        assertNotNull(state.errorKind)
    }
}
