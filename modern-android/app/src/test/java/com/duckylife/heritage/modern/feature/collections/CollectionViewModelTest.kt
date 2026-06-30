package com.duckylife.heritage.modern.feature.collections

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `empty collection loads without error`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CollectionViewModel(
            id = "col-1",
            type = null,
            topicKey = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.collection)
        assertNull(state.errorKind)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("not found"))
        val viewModel = CollectionViewModel(
            id = "missing",
            type = null,
            topicKey = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.collection)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `missing identifier sets errorKind`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CollectionViewModel(
            id = null,
            type = null,
            topicKey = null,
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.collection)
        assertNotNull(state.errorKind)
    }
}
