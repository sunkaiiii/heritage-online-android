package com.duckylife.heritage.modern.feature.research

import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.research.model.ResearchReportDetailUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResearchReportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeResearchRepository()

    @Test
    fun `init loads detail when reportId is provided in SavedStateHandle`() = runTest {
        fakeRepository.reportDetail = reportDetail("r1")
        val savedStateHandle = SavedStateHandle().apply {
            set("research_report_id", "r1")
        }

        val viewModel = ResearchReportViewModel(fakeRepository, savedStateHandle)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.detail.isLoading)
        assertEquals("r1", viewModel.uiState.value.detail.data?.reportId)
        assertEquals("r1", viewModel.uiState.value.reportId)
    }

    @Test
    fun `setReportId loads new detail and updates SavedStateHandle`() = runTest {
        fakeRepository.reportDetail = reportDetail("r2")
        val savedStateHandle = SavedStateHandle()

        val viewModel = ResearchReportViewModel(fakeRepository, savedStateHandle)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.detail.data == null)

        viewModel.setReportId("r2")
        advanceUntilIdle()

        assertEquals("r2", viewModel.uiState.value.reportId)
        assertEquals("r2", savedStateHandle.get<String>("research_report_id"))
        assertEquals("r2", viewModel.uiState.value.detail.data?.reportId)
    }

    @Test
    fun `setReportId ignores blank and duplicate ids`() = runTest {
        fakeRepository.reportDetail = reportDetail("r1")
        val savedStateHandle = SavedStateHandle()

        val viewModel = ResearchReportViewModel(fakeRepository, savedStateHandle)
        viewModel.setReportId("r1")
        advanceUntilIdle()
        assertEquals(1, fakeRepository.reportDetailCalls)

        viewModel.setReportId("r1")
        advanceUntilIdle()
        assertEquals(1, fakeRepository.reportDetailCalls)

        viewModel.setReportId("")
        advanceUntilIdle()
        assertEquals(1, fakeRepository.reportDetailCalls)
    }

    @Test
    fun `loadDetail maps error to AsyncState errorKind`() = runTest {
        fakeRepository.failure = IllegalStateException("network error")
        val savedStateHandle = SavedStateHandle().apply {
            set("research_report_id", "r1")
        }

        val viewModel = ResearchReportViewModel(fakeRepository, savedStateHandle)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.detail.errorKind)
    }

    @Test
    fun `retry reloads detail`() = runTest {
        fakeRepository.reportDetail = reportDetail("r1")
        val savedStateHandle = SavedStateHandle().apply {
            set("research_report_id", "r1")
        }

        val viewModel = ResearchReportViewModel(fakeRepository, savedStateHandle)
        advanceUntilIdle()
        assertEquals(1, fakeRepository.reportDetailCalls)

        viewModel.retry()
        advanceUntilIdle()

        assertEquals(2, fakeRepository.reportDetailCalls)
    }

    private fun reportDetail(id: String) = ResearchReportDetailUiModel(
        reportId = id,
        packageId = null,
        title = "Report $id",
        status = ResearchTaskStatus.Succeeded,
        createdAt = null,
        executiveSummary = "Summary",
        findings = emptyList(),
        sourceCount = 0,
        limitations = emptyList(),
        warnings = emptyList(),
        followUpQuestions = emptyList(),
    )

    private class FakeResearchRepository : ResearchRepository {
        var reportDetail: ResearchReportDetailUiModel? = null
        var failure: Throwable? = null
        var reportDetailCalls: Int = 0

        override suspend fun getReportDetail(reportId: String): ResearchReportDetailUiModel {
            reportDetailCalls++
            failure?.let { throw it }
            return reportDetail ?: error("No report detail set")
        }

        override suspend fun getPackages() = error("not used")
        override suspend fun getPackageDetail(packageId: String) = error("not used")
        override suspend fun getArtifactContent(packageId: String, artifactName: String) = error("not used")
        override suspend fun getArtifactBytes(packageId: String, artifactName: String) = error("not used")
        override suspend fun saveArtifactToCache(
            packageId: String,
            artifactName: String,
            bytes: ByteArray,
        ) = error("not used")

        override suspend fun getReports() = error("not used")
        override suspend fun getReportByPackage(packageId: String) = error("not used")
    }
}
