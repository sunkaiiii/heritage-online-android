package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DirectoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startsWithNationalProjectKind() = runTest {
        val viewModel = DirectoryViewModel(
            repository = FakeHeritageRepository(),
        )

        assertEquals(DirectoryItemKind.NationalProject, viewModel.uiState.value.selectedKind)
    }

    @Test
    fun selectKindUpdatesSelectedKind() = runTest {
        val viewModel = DirectoryViewModel(
            repository = FakeHeritageRepository(),
        )

        viewModel.selectKind(DirectoryItemKind.CulturalEcoZone)
        advanceUntilIdle()

        assertEquals(DirectoryItemKind.CulturalEcoZone, viewModel.uiState.value.selectedKind)
    }
}
