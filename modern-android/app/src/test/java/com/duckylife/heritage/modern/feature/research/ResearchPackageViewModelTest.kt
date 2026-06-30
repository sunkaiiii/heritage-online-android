package com.duckylife.heritage.modern.feature.research

import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.research.model.ResearchArtifactUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageDetailUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportDetailUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResearchPackageViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeResearchRepository()

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ) = ResearchPackageViewModel(fakeRepository, savedStateHandle)

    @Test
    fun `init loads detail when packageId is restored from SavedStateHandle`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")
        val savedStateHandle = SavedStateHandle().apply {
            set("research_package_id", "p1")
        }

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        assertEquals("p1", viewModel.uiState.value.packageId)
        assertFalse(viewModel.uiState.value.detail.isLoading)
        assertEquals("p1", viewModel.uiState.value.detail.data?.packageId)
    }

    @Test
    fun `setPackageId loads detail and updates SavedStateHandle`() = runTest {
        fakeRepository.packageDetail = packageDetail("p2")
        val savedStateHandle = SavedStateHandle()

        val viewModel = ResearchPackageViewModel(fakeRepository, savedStateHandle)
        viewModel.setPackageId("p2")
        advanceUntilIdle()

        assertEquals("p2", savedStateHandle.get<String>("research_package_id"))
        assertEquals("p2", viewModel.uiState.value.detail.data?.packageId)
    }

    @Test
    fun `successful detail with succeeded status checks report`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1", status = ResearchTaskStatus.Succeeded)
        fakeRepository.reportByPackage = reportDetail("rp1")

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.detail.data?.hasReport == true)
        assertEquals("rp1", viewModel.uiState.value.detail.data?.reportId)
        assertEquals(1, fakeRepository.reportByPackageCalls)
    }

    @Test
    fun `detail with non succeeded status does not check report`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1", status = ResearchTaskStatus.Running)

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()

        assertEquals(0, fakeRepository.reportByPackageCalls)
        assertTrue(viewModel.uiState.value.detail.data?.hasReport == false)
    }

    @Test
    fun `loadArtifact updates artifactContent`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")
        fakeRepository.artifactContent = "artifact content"

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()

        viewModel.loadArtifact("summary.md")
        advanceUntilIdle()

        assertEquals("artifact content", viewModel.uiState.value.artifactContent.data)
        assertEquals("p1" to "summary.md", fakeRepository.capturedArtifactRequest)
    }

    @Test
    fun `clearArtifactContent resets state`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")
        fakeRepository.artifactContent = "content"

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        viewModel.loadArtifact("summary.md")
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.artifactContent.data)

        viewModel.clearArtifactContent()

        assertNull(viewModel.uiState.value.artifactContent.data)
        assertFalse(viewModel.uiState.value.artifactContent.isLoading)
    }

    @Test
    fun `shareArtifact emits Text payload for small text artifact`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")
        fakeRepository.artifactContent = "# Summary"

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()

        val payload = async { viewModel.shareEvent.first() }
        viewModel.shareArtifact(textArtifact("summary.md", sizeBytes = 100))
        advanceUntilIdle()

        val result = payload.await()
        assertTrue(result is SharePayload.Text)
        assertEquals("# Summary", (result as SharePayload.Text).content)
    }

    @Test
    fun `shareArtifact emits File payload for binary artifact`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")
        val tempFile = File.createTempFile("artifact", ".zip").apply { deleteOnExit() }
        fakeRepository.cacheFile = tempFile

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()

        val payload = async { viewModel.shareEvent.first() }
        viewModel.shareArtifact(binaryArtifact("data.zip"))
        advanceUntilIdle()

        val result = payload.await()
        assertTrue(result is SharePayload.File)
        assertEquals(tempFile, (result as SharePayload.File).file)
        assertEquals("application/zip", result.mimeType)
    }

    @Test
    fun `shareArtifact maps error to artifactContent errorKind`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")
        fakeRepository.failure = IllegalStateException("download failed")

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()

        viewModel.shareArtifact(textArtifact("summary.md"))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.artifactContent.errorKind)
    }

    @Test
    fun `retry reloads detail`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        advanceUntilIdle()
        assertEquals(1, fakeRepository.packageDetailCalls)

        viewModel.retry()
        advanceUntilIdle()

        assertEquals(2, fakeRepository.packageDetailCalls)
    }

    @Test
    fun `loadDetail ignores stale result after packageId changes`() = runTest {
        fakeRepository.packageDetail = packageDetail("p1")

        val viewModel = createViewModel()
        viewModel.setPackageId("p1")
        // Do not advance; immediately change id before load completes
        viewModel.setPackageId("p2")
        advanceUntilIdle()

        assertEquals("p2", viewModel.uiState.value.packageId)
        assertEquals(2, fakeRepository.packageDetailCalls)
    }

    private fun packageDetail(
        id: String,
        status: ResearchTaskStatus = ResearchTaskStatus.Succeeded,
    ) = ResearchPackageDetailUiModel(
        packageId = id,
        title = "Package $id",
        querySummary = null,
        sourceType = com.duckylife.heritage.modern.feature.research.model.ResearchSourceType.GraphRagPack,
        sourceDetail = null,
        dataScope = emptyList(),
        createdAt = null,
        status = status,
        nodeCount = 0,
        edgeCount = 0,
        sourceCount = 0,
        evidenceCount = 0,
        artifacts = emptyList(),
        hasReport = false,
        reportId = null,
        warnings = emptyList(),
    )

    private fun reportDetail(id: String) = ResearchReportDetailUiModel(
        reportId = id,
        packageId = "p1",
        title = "Report $id",
        status = ResearchTaskStatus.Succeeded,
        createdAt = null,
        executiveSummary = "",
        findings = emptyList(),
        sourceCount = 0,
        limitations = emptyList(),
        warnings = emptyList(),
        followUpQuestions = emptyList(),
    )

    private fun textArtifact(name: String, sizeBytes: Long = 100) = ResearchArtifactUiModel(
        name = name,
        displayName = name,
        artifactType = "summary",
        mimeType = "text/markdown",
        sizeBytes = sizeBytes,
        isViewable = true,
    )

    private fun binaryArtifact(name: String) = ResearchArtifactUiModel(
        name = name,
        displayName = name,
        artifactType = "data",
        mimeType = "application/zip",
        sizeBytes = 100,
        isViewable = false,
    )

    private class FakeResearchRepository : ResearchRepository {
        var packageDetail: ResearchPackageDetailUiModel? = null
        var reportByPackage: ResearchReportDetailUiModel? = null
        var artifactContent: String = ""
        var cacheFile: File? = null
        var failure: Throwable? = null

        var packageDetailCalls: Int = 0
        var reportByPackageCalls: Int = 0
        var capturedArtifactRequest: Pair<String, String>? = null

        override suspend fun getPackages() = error("not used")

        override suspend fun getPackageDetail(packageId: String): ResearchPackageDetailUiModel {
            packageDetailCalls++
            failure?.let { throw it }
            return packageDetail ?: error("No package detail set")
        }

        override suspend fun getArtifactContent(packageId: String, artifactName: String): String {
            capturedArtifactRequest = packageId to artifactName
            failure?.let { throw it }
            return artifactContent
        }

        override suspend fun getArtifactBytes(packageId: String, artifactName: String): ByteArray {
            failure?.let { throw it }
            return byteArrayOf(0x50, 0x4B)
        }

        override suspend fun saveArtifactToCache(
            packageId: String,
            artifactName: String,
            bytes: ByteArray,
        ): File {
            failure?.let { throw it }
            return cacheFile ?: error("No cache file set")
        }

        override suspend fun getReports() = error("not used")

        override suspend fun getReportDetail(reportId: String) = error("not used")

        override suspend fun getReportByPackage(packageId: String): ResearchReportDetailUiModel {
            reportByPackageCalls++
            failure?.let { throw it }
            return reportByPackage ?: error("No report set")
        }
    }
}
