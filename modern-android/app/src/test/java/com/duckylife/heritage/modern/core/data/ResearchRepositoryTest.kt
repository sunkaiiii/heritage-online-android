package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.ResearchArtifactQuery
import com.duckylife.heritage.modern.core.network.ResearchPackageDetailQuery
import com.duckylife.heritage.modern.core.network.ResearchReportByPackageQuery
import com.duckylife.heritage.modern.core.network.ResearchReportDetailQuery
import com.duckylife.heritage.modern.core.network.api.ResearchApi
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageArtifactDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageCreateRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageListResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportFindingDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportListResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResearchRepositoryTest {

    private val fakeApi = FakeResearchApi()
    private val repository: ResearchRepository = DefaultResearchRepository(api = fakeApi)

    @Test
    fun `getPackages maps all status branches and only succeeded is clickable`() = runTest {
        fakeApi.packages = listOf(
            packageDto("p1", ResearchTaskStatus.Succeeded),
            packageDto("p2", ResearchTaskStatus.Running),
            packageDto("p3", ResearchTaskStatus.Failed),
            packageDto("p4", ResearchTaskStatus.Queued),
            packageDto("p5", ResearchTaskStatus.Cancelled),
            packageDto("p6", ResearchTaskStatus.Unknown),
        )

        val result = repository.getPackages()

        assertEquals(6, result.size)
        result.forEachIndexed { index, item ->
            assertEquals(fakeApi.packages[index].packageId, item.packageId)
            assertEquals(fakeApi.packages[index].status, item.status)
        }
        assertTrue(result[0].isClickable) // Succeeded
        assertFalse(result[1].isClickable) // Running
        assertFalse(result[2].isClickable) // Failed
        assertFalse(result[3].isClickable) // Queued
        assertFalse(result[4].isClickable) // Cancelled
        assertFalse(result[5].isClickable) // Unknown
    }

    @Test
    fun `getPackageDetail maps artifacts and counts`() = runTest {
        fakeApi.packageDetail = ResearchPackageDto(
            packageId = "p1",
            status = ResearchTaskStatus.Succeeded,
            graphRagPackId = "pack-1",
            request = ResearchPackageCreateRequestDto(
                graphRagPackId = "pack-1",
                includeContent = true,
                includeEvidence = true,
                includeAiResults = true,
                includeAiInferred = true,
            ),
            artifacts = listOf(
                artifact("content.json", "content", "application/json", 1024),
                artifact("graph.json", "graph", "application/json", 2048),
                artifact("sources.json", "sources", "application/json", 512),
                artifact("evidence.json", "evidence", "application/json", 256),
                artifact("report.zip", "report", "application/zip", 4096),
            ),
            createdAt = "2026-06-01T00:00:00Z",
        )

        val detail = repository.getPackageDetail("p1")

        assertEquals("p1", detail.packageId)
        assertEquals(ResearchTaskStatus.Succeeded, detail.status)
        assertEquals(1, detail.nodeCount)
        assertEquals(1, detail.edgeCount)
        assertEquals(1, detail.sourceCount)
        assertEquals(1, detail.evidenceCount)
        assertEquals(5, detail.artifacts.size)
        assertTrue(detail.artifacts.any { it.name == "content.json" && it.isViewable })
        assertFalse(detail.artifacts.any { it.name == "report.zip" && it.isViewable })
        assertEquals("pack-1", detail.source)
    }

    @Test
    fun `package mappers omit unsafe artifact names from counts and details`() = runTest {
        val dto = ResearchPackageDto(
            packageId = "p1",
            status = ResearchTaskStatus.Succeeded,
            graphRagPackId = "pack-1",
            artifacts = listOf(
                artifact("content.json", "content", "application/json", 1024),
                artifact("../secret.json", "content", "application/json", 2048),
            ),
        )
        fakeApi.packages = listOf(dto)
        fakeApi.packageDetail = dto

        val listItem = repository.getPackages().single()
        val detail = repository.getPackageDetail("p1")

        assertEquals(1, listItem.artifactCount)
        assertEquals(1, detail.nodeCount)
        assertEquals(listOf("content.json"), detail.artifacts.map { it.name })
    }

    @Test
    fun `getReports maps reports and title fallback`() = runTest {
        fakeApi.reports = listOf(
            ResearchReportDto(
                reportId = "r1",
                packageId = "p1",
                status = ResearchTaskStatus.Succeeded,
                title = "有标题报告",
                createdAt = "2026-06-02T00:00:00Z",
                modelName = "gpt-4o",
            ),
            ResearchReportDto(
                reportId = "r2",
                packageId = "p2",
                status = ResearchTaskStatus.Running,
                title = "",
                createdAt = null,
                modelName = "",
            ),
        )

        val result = repository.getReports()

        assertEquals(2, result.size)
        assertEquals("有标题报告", result[0].title)
        assertEquals("r2", result[1].title) // fallback to reportId
        assertEquals(ResearchTaskStatus.Running, result[1].status)
        assertNull(result[1].createdAt)
    }

    @Test
    fun `getReportDetail maps findings with evidence and title fallback`() = runTest {
        fakeApi.reportDetail = ResearchReportDto(
            reportId = "r1",
            packageId = "p1",
            status = ResearchTaskStatus.Succeeded,
            title = "",
            executiveSummary = "摘要",
            findings = listOf(
                ResearchReportFindingDto(
                    findingId = "f1",
                    claim = "发现一的内容\n第二行",
                    evidenceIds = listOf("e1", "e2"),
                    confidence = 0.95,
                    limitations = listOf("样本有限"),
                ),
                ResearchReportFindingDto(
                    findingId = "f2",
                    claim = "",
                    evidenceIds = emptyList(),
                    confidence = 0.0,
                ),
            ),
            limitations = listOf(" limitation "),
            warnings = listOf(" warning "),
            followUpQuestions = listOf("q1"),
        )

        val detail = repository.getReportDetail("r1")

        assertEquals("r1", detail.title) // fallback
        assertEquals("摘要", detail.executiveSummary)
        assertEquals(2, detail.findings.size)
        assertEquals(1, detail.findings[0].number)
        assertEquals("发现一的内容", detail.findings[0].title) // first line
        assertEquals(2, detail.findings[0].evidence.size)
        assertEquals(0.95, detail.findings[0].confidence ?: 0.0, 0.001)
        assertEquals(2, detail.sourceCount)
        assertNull(detail.findings[1].title) // UI localizes the fallback title
    }

    @Test
    fun `getReportByPackage delegates to report by package endpoint`() = runTest {
        fakeApi.reportByPackage = ResearchReportDto(
            reportId = "r1",
            packageId = "p1",
            status = ResearchTaskStatus.Succeeded,
            title = "包内报告",
            executiveSummary = "",
        )

        val detail = repository.getReportByPackage("p1")

        assertEquals("r1", detail.reportId)
        assertEquals("包内报告", detail.title)
        assertEquals("p1", fakeApi.capturedReportByPackageQuery?.packageId)
    }

    @Test
    fun `getArtifactContent passes safe name to api`() = runTest {
        fakeApi.artifactContent = "artifact body"

        val content = repository.getArtifactContent("p1", "manifest.json")

        assertEquals("artifact body", content)
        assertEquals("p1", fakeApi.capturedArtifactQuery?.packageId)
        assertEquals("manifest.json", fakeApi.capturedArtifactQuery?.artifactName)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getArtifactContent rejects path traversal name`() = runTest {
        repository.getArtifactContent("p1", "../../etc/passwd")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getArtifactContent rejects empty name`() = runTest {
        repository.getArtifactContent("p1", "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getArtifactContent rejects name with backslash`() = runTest {
        repository.getArtifactContent("p1", "my\\file.txt")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getArtifactContent rejects dot traversal name`() = runTest {
        repository.getArtifactContent("p1", "..")
    }

    @Test
    fun `getArtifactContent allows unicode and spaces`() = runTest {
        fakeApi.artifactContent = "ok"

        val content = repository.getArtifactContent("p1", "报告 2026.json")

        assertEquals("ok", content)
        assertEquals("报告 2026.json", fakeApi.capturedArtifactQuery?.artifactName)
    }

    private fun packageDto(
        id: String,
        status: ResearchTaskStatus,
    ): ResearchPackageDto = ResearchPackageDto(
        packageId = id,
        status = status,
        graphRagPackId = "pack-$id",
    )

    private fun artifact(
        name: String,
        type: String,
        mimeType: String,
        size: Long,
    ): ResearchPackageArtifactDto = ResearchPackageArtifactDto(
        name = name,
        artifactType = type,
        mimeType = mimeType,
        sizeBytes = size,
        sha256 = "sha256-$name",
    )

    private class FakeResearchApi : ResearchApi {
        var packages: List<ResearchPackageDto> = emptyList()
        var packageDetail: ResearchPackageDto = ResearchPackageDto(
            packageId = "",
            status = ResearchTaskStatus.Unknown,
        )
        var reports: List<ResearchReportDto> = emptyList()
        var reportDetail: ResearchReportDto = ResearchReportDto(
            reportId = "",
            status = ResearchTaskStatus.Unknown,
        )
        var reportByPackage: ResearchReportDto = ResearchReportDto(
            reportId = "",
            status = ResearchTaskStatus.Unknown,
        )
        var artifactContent: String = ""

        var capturedPackageDetailQuery: ResearchPackageDetailQuery? = null
        var capturedArtifactQuery: ResearchArtifactQuery? = null
        var capturedReportDetailQuery: ResearchReportDetailQuery? = null
        var capturedReportByPackageQuery: ResearchReportByPackageQuery? = null

        override suspend fun getResearchPackages(): ResearchPackageListResultDto =
            ResearchPackageListResultDto(packages = packages, totalCount = packages.size)

        override suspend fun getResearchPackageDetail(query: ResearchPackageDetailQuery): ResearchPackageDto {
            capturedPackageDetailQuery = query
            return packageDetail.copy(packageId = query.packageId)
        }

        override suspend fun getResearchArtifact(query: ResearchArtifactQuery): String {
            capturedArtifactQuery = query
            return artifactContent
        }

        override suspend fun getResearchReports(): ResearchReportListResultDto =
            ResearchReportListResultDto(reports = reports, totalCount = reports.size)

        override suspend fun getResearchReportDetail(query: ResearchReportDetailQuery): ResearchReportDto {
            capturedReportDetailQuery = query
            return reportDetail.copy(reportId = query.reportId)
        }

        override suspend fun getResearchReportByPackage(query: ResearchReportByPackageQuery): ResearchReportDto {
            capturedReportByPackageQuery = query
            return reportByPackage.copy(packageId = query.packageId)
        }
    }
}
