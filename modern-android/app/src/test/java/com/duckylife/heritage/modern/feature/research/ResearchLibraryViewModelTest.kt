package com.duckylife.heritage.modern.feature.research

import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportItemUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResearchLibraryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeResearchRepository()

    @Test
    fun `loads packages and reports on init`() = runTest {
        fakeRepository.packages = listOf(packageItem("p1"))
        fakeRepository.reports = listOf(reportItem("r1"))

        val viewModel = ResearchLibraryViewModel(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.packages.isLoading)
        assertFalse(state.reports.isLoading)
        assertEquals(1, state.packages.data?.size)
        assertEquals(1, state.reports.data?.size)
        assertEquals(ResearchLibraryTab.Packages, state.selectedTab)
    }

    @Test
    fun `selectTab switches to reports and loads if missing`() = runTest {
        fakeRepository.packages = listOf(packageItem("p1"))
        fakeRepository.reports = listOf(reportItem("r1"))

        val viewModel = ResearchLibraryViewModel(fakeRepository)
        advanceUntilIdle()
        viewModel.selectTab(ResearchLibraryTab.Reports)
        advanceUntilIdle()

        assertEquals(ResearchLibraryTab.Reports, viewModel.uiState.value.selectedTab)
        assertEquals(1, viewModel.uiState.value.reports.data?.size)
    }

    @Test
    fun `selectTab does not reload already loaded data`() = runTest {
        fakeRepository.packages = listOf(packageItem("p1"))

        val viewModel = ResearchLibraryViewModel(fakeRepository)
        advanceUntilIdle()
        assertEquals(1, fakeRepository.packagesCalls)

        viewModel.selectTab(ResearchLibraryTab.Packages)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.packagesCalls)
    }

    @Test
    fun `refresh reloads current tab`() = runTest {
        fakeRepository.packages = listOf(packageItem("p1"))

        val viewModel = ResearchLibraryViewModel(fakeRepository)
        advanceUntilIdle()
        assertEquals(1, fakeRepository.packagesCalls)

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(2, fakeRepository.packagesCalls)
    }

    @Test
    fun `loadPackages maps error to AsyncState errorKind`() = runTest {
        fakeRepository.failure = IllegalStateException("network error")

        val viewModel = ResearchLibraryViewModel(fakeRepository)
        advanceUntilIdle()

        assertEquals(ErrorKind.Unknown, viewModel.uiState.value.packages.errorKind)
        assertTrue(viewModel.uiState.value.packages.data.isNullOrEmpty())
    }

    @Test
    fun `loadReports maps error to AsyncState errorKind`() = runTest {
        fakeRepository.failure = IllegalStateException("network error")

        val viewModel = ResearchLibraryViewModel(fakeRepository)
        advanceUntilIdle()

        assertEquals(ErrorKind.Unknown, viewModel.uiState.value.reports.errorKind)
    }

    private fun packageItem(id: String) = ResearchPackageItemUiModel(
        packageId = id,
        title = "Package $id",
        subtitle = null,
        createdAt = null,
        status = ResearchTaskStatus.Succeeded,
        isClickable = true,
        artifactCount = 0,
        includesContent = true,
        includesEvidence = false,
        includesAiResults = false,
        includesAiInferred = false,
    )

    private fun reportItem(id: String) = ResearchReportItemUiModel(
        reportId = id,
        packageId = null,
        title = "Report $id",
        status = ResearchTaskStatus.Succeeded,
        createdAt = null,
        modelName = null,
    )

    private class FakeResearchRepository : ResearchRepository {
        var packages: List<ResearchPackageItemUiModel> = emptyList()
        var reports: List<ResearchReportItemUiModel> = emptyList()
        var failure: Throwable? = null
        var packagesCalls: Int = 0
        var reportsCalls: Int = 0

        override suspend fun getPackages(): List<ResearchPackageItemUiModel> {
            packagesCalls++
            failure?.let { throw it }
            return packages
        }

        override suspend fun getReports(): List<ResearchReportItemUiModel> {
            reportsCalls++
            failure?.let { throw it }
            return reports
        }

        override suspend fun getPackageDetail(packageId: String) = error("not used")
        override suspend fun getArtifactContent(packageId: String, artifactName: String) = error("not used")
        override suspend fun getArtifactBytes(packageId: String, artifactName: String) = error("not used")
        override suspend fun saveArtifactToCache(
            packageId: String,
            artifactName: String,
            bytes: ByteArray,
        ) = error("not used")

        override suspend fun getReportDetail(reportId: String) = error("not used")
        override suspend fun getReportByPackage(packageId: String) = error("not used")
    }
}
