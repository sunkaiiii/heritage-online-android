package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.ResearchArtifactQuery
import com.duckylife.heritage.modern.core.network.ResearchPackageDetailQuery
import com.duckylife.heritage.modern.core.network.ResearchReportByPackageQuery
import com.duckylife.heritage.modern.core.network.ResearchReportDetailQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportSummaryDto

/**
 * 已生成研究资料包与研究报告只读端点契约。
 */
interface ResearchApi {
    suspend fun getResearchPackages(): List<ResearchPackageSummaryDto>

    suspend fun getResearchPackageDetail(query: ResearchPackageDetailQuery): ResearchPackageDetailDto

    suspend fun getResearchArtifact(query: ResearchArtifactQuery): String

    suspend fun getResearchReports(): List<ResearchReportSummaryDto>

    suspend fun getResearchReportDetail(query: ResearchReportDetailQuery): ResearchReportDetailDto

    suspend fun getResearchReportByPackage(query: ResearchReportByPackageQuery): ResearchReportDetailDto
}
