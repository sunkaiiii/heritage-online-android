package com.duckylife.heritage.modern.feature.compare

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.CompareType
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
class CompareViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `empty input sets errorMessage`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateLeft("")
        viewModel.updateRight("")
        viewModel.compare()

        val state = viewModel.uiState.value
        assertEquals("empty", state.errorMessage)
        assertNull(state.result)
    }

    @Test
    fun `blank input sets errorMessage`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateLeft("  ")
        viewModel.updateRight("北京")
        viewModel.compare()

        val state = viewModel.uiState.value
        assertEquals("empty", state.errorMessage)
    }

    @Test
    fun `same input sets errorMessage`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateLeft("北京")
        viewModel.updateRight("北京")
        viewModel.compare()

        val state = viewModel.uiState.value
        assertEquals("same", state.errorMessage)
        assertNull(state.result)
    }

    @Test
    fun `same input case insensitive sets errorMessage`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateLeft("NationalProject")
        viewModel.updateRight("nationalproject")
        viewModel.compare()

        val state = viewModel.uiState.value
        assertEquals("same", state.errorMessage)
    }

    @Test
    fun `invalid kind sets errorMessage`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateType(CompareType.Kind)
        viewModel.updateLeft("InvalidKind")
        viewModel.updateRight("AnotherInvalid")
        viewModel.compare()

        val state = viewModel.uiState.value
        assertEquals("invalid_kind", state.errorMessage)
        assertNull(state.result)
    }

    @Test
    fun `compare regions succeeds`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateType(CompareType.Region)
        viewModel.updateLeft("北京")
        viewModel.updateRight("上海")
        viewModel.compare()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.errorKind)
        assertNotNull(state.result)
    }

    @Test
    fun `compare categories succeeds`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateType(CompareType.Category)
        viewModel.updateLeft("传统技艺")
        viewModel.updateRight("民俗")
        viewModel.compare()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.errorKind)
        assertNotNull(state.result)
    }

    @Test
    fun `compare kinds succeeds with valid kind names`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateType(CompareType.Kind)
        viewModel.updateLeft("NationalProject")
        viewModel.updateRight("UnescoEntry")
        viewModel.compare()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.errorKind)
        assertNotNull(state.result)
    }

    @Test
    fun `compare kinds succeeds with wire names`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateType(CompareType.Kind)
        viewModel.updateLeft("nationalProject")
        viewModel.updateRight("culturalEcoZone")
        viewModel.compare()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertNull(state.errorKind)
        assertNotNull(state.result)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server error"))
        val viewModel = CompareViewModel(repository = repo)

        viewModel.updateType(CompareType.Region)
        viewModel.updateLeft("北京")
        viewModel.updateRight("上海")
        viewModel.compare()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `clearError resets error state`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        // Trigger an error
        viewModel.updateLeft("")
        viewModel.updateRight("")
        viewModel.compare()
        assertEquals("empty", viewModel.uiState.value.errorMessage)

        // Clear it
        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.errorKind)
    }

    @Test
    fun `updateType resets result and error`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = CompareViewModel(repository = repo)

        // Trigger an error first
        viewModel.updateLeft("北京")
        viewModel.updateRight("北京")
        viewModel.compare()
        assertEquals("same", viewModel.uiState.value.errorMessage)

        // Changing type should reset result and error
        viewModel.updateType(CompareType.Category)
        val state = viewModel.uiState.value
        assertNull(state.result)
        assertNull(state.errorKind)
        assertNull(state.errorMessage)
    }
}
