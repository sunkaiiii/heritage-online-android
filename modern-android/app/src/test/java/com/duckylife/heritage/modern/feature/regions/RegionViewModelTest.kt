package com.duckylife.heritage.modern.feature.regions

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
class RegionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `empty atlas loads without error`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = RegionViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.atlas)
        assertNull(state.errorKind)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("network down"))
        val viewModel = RegionViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.atlas)
        assertNotNull(state.errorKind)
    }
}
