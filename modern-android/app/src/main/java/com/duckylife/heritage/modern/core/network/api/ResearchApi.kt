package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.ResearchArtifactQuery
import com.duckylife.heritage.modern.core.network.ResearchPackageDetailQuery
import com.duckylife.heritage.modern.core.network.ResearchReportByPackageQuery
import com.duckylife.heritage.modern.core.network.ResearchReportDetailQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageListResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportListResultDto

/**
 * 已生成研究资料包与研究报告只读端点契约。
 */
interface ResearchApi {
    suspend fun getResearchPackages(): ResearchPackageListResultDto

    suspend fun getResearchPackageDetail(query: ResearchPackageDetailQuery): ResearchPackageDto

    suspend fun getResearchArtifact(query: ResearchArtifactQuery): String

    suspend fun getResearchReports(): ResearchReportListResultDto

    suspend fun getResearchReportDetail(query: ResearchReportDetailQuery): ResearchReportDto

    suspend fun getResearchReportByPackage(query: ResearchReportByPackageQuery): ResearchReportDto
}
