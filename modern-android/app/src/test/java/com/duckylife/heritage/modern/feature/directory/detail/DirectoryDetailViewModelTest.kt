package com.duckylife.heritage.modern.feature.directory.detail

import com.duckylife.heritage.modern.core.data.DirectoryDetailLookup
import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.profile.FakeLocalUserSyncRepository
import com.duckylife.heritage.modern.core.saved.FakeSavedContentRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DirectoryDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshLoadsDirectoryDetail() = runTest {
        val viewModel = DirectoryDetailViewModel(
            itemId = "item-1",
            sourceId = null,
            kind = DirectoryItemKind.NationalProject,
            repository = FakeHeritageRepository(
                directoryDetails = mapOf(
                    "item-1" to DirectoryItemDetailDto(
                        id = "item-1",
                        kind = DirectoryItemKind.NationalProject,
                        title = "两当号子",
                    ),
                ),
            ),
            savedContentRepository = FakeSavedContentRepository(),
            syncRepository = FakeLocalUserSyncRepository(),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertEquals("item-1", state.item?.id)
        assertEquals("两当号子", state.item?.title)
    }

    @Test
    fun refreshLoadsDirectoryDetailBySourceId() = runTest {
        val repository = FakeHeritageRepository(
            directoryDetailsBySourceId = mapOf(
                "source-1" to DirectoryItemDetailDto(
                    id = "item-2",
                    kind = DirectoryItemKind.CulturalEcoZone,
                    title = "文化生态区",
                ),
            ),
        )
        val viewModel = DirectoryDetailViewModel(
            itemId = null,
            sourceId = "source-1",
            kind = DirectoryItemKind.CulturalEcoZone,
            repository = repository,
            savedContentRepository = FakeSavedContentRepository(),
            syncRepository = FakeLocalUserSyncRepository(),
        )

        advanceUntilIdle()

        assertEquals("item-2", viewModel.uiState.value.item?.id)
        assertEquals(
            listOf("source-1" to DirectoryItemKind.CulturalEcoZone),
            repository.directorySourceIdQueries,
        )
    }

    @Test
    fun showsCachedDirectoryDetailWhenRefreshFails() = runTest {
        val lookup = DirectoryDetailLookup(itemId = "item-1")
        val viewModel = DirectoryDetailViewModel(
            itemId = "item-1",
            sourceId = null,
            kind = DirectoryItemKind.NationalProject,
            repository = FakeHeritageRepository(
                cachedDirectoryDetails = mapOf(
                    lookup to DirectoryItemDetailDto(
                        id = "item-1",
                        kind = DirectoryItemKind.NationalProject,
                        title = "缓存名录",
                    ),
                ),
                failure = IllegalStateException("network down"),
            ),
            savedContentRepository = FakeSavedContentRepository(),
            syncRepository = FakeLocalUserSyncRepository(),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertEquals("缓存名录", state.item?.title)
    }
}
