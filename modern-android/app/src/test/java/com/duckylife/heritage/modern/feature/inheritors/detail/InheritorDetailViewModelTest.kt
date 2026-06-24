package com.duckylife.heritage.modern.feature.inheritors.detail

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.InheritorDetailLookup
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
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
class InheritorDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshLoadsInheritorDetail() = runTest {
        val viewModel = InheritorDetailViewModel(
            inheritorId = "inheritor-1",
            sourceId = null,
            repository = FakeHeritageRepository(
                inheritorDetails = mapOf(
                    "inheritor-1" to InheritorDetailDto(
                        id = "inheritor-1",
                        name = "张三",
                        projectName = "剪纸",
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
        assertEquals("inheritor-1", state.item?.id)
        assertEquals("张三", state.item?.name)
    }

    @Test
    fun refreshLoadsInheritorDetailBySourceId() = runTest {
        val repository = FakeHeritageRepository(
            inheritorDetailsBySourceId = mapOf(
                "source-1" to InheritorDetailDto(
                    id = "inheritor-2",
                    name = "李四",
                    projectName = "皮影戏",
                ),
            ),
        )
        val viewModel = InheritorDetailViewModel(
            inheritorId = null,
            sourceId = "source-1",
            repository = repository,
            savedContentRepository = FakeSavedContentRepository(),
            syncRepository = FakeLocalUserSyncRepository(),
        )

        advanceUntilIdle()

        assertEquals("inheritor-2", viewModel.uiState.value.item?.id)
        assertEquals(listOf("source-1"), repository.inheritorSourceIdQueries)
    }

    @Test
    fun showsCachedInheritorDetailWhenRefreshFails() = runTest {
        val lookup = InheritorDetailLookup(inheritorId = "inheritor-1")
        val viewModel = InheritorDetailViewModel(
            inheritorId = "inheritor-1",
            sourceId = null,
            repository = FakeHeritageRepository(
                cachedInheritorDetails = mapOf(
                    lookup to InheritorDetailDto(
                        id = "inheritor-1",
                        name = "缓存传承人",
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
        assertEquals("缓存传承人", state.item?.name)
    }
}
