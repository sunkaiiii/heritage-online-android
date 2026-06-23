package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.LearningRouteBuildQuery
import com.duckylife.heritage.modern.core.network.LearningRouteDetailQuery
import com.duckylife.heritage.modern.core.network.LearningRouteNextQuery
import com.duckylife.heritage.modern.core.network.LearningRoutesListQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteNextDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSummaryDto

/**
 * 新学习路线端点契约。
 */
interface LearningRoutesApi {
    suspend fun getLearningRoutes(query: LearningRoutesListQuery = LearningRoutesListQuery()): List<LearningRouteSummaryDto>

    suspend fun getLearningRouteDetail(query: LearningRouteDetailQuery): LearningRouteDetailDto

    suspend fun buildLearningRoute(query: LearningRouteBuildQuery): LearningRouteDetailDto

    suspend fun getLearningRouteNextStep(query: LearningRouteNextQuery): LearningRouteNextDto
}
