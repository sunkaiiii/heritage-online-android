package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.LearningRouteBuildQuery
import com.duckylife.heritage.modern.core.network.LearningRouteDetailQuery
import com.duckylife.heritage.modern.core.network.LearningRouteNextQuery
import com.duckylife.heritage.modern.core.network.LearningRoutesListQuery
import com.duckylife.heritage.modern.core.network.api.LearningRoutesApi
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteNextDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteStepDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSummaryDto
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteNextUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSectionUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteStepUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import javax.inject.Inject

/**
 * 新学习路线只读仓库。
 *
 * 负责把 [LearningRoutesApi] 返回的 DTO 映射为 UI 友好的领域模型，
 * 并在 `next` 等需要用户身份的接口上自动注入当前 profile ID。
 */
interface LearningRoutesRepository {
    suspend fun getRoutes(
        difficulty: LearningRouteDifficulty = LearningRouteDifficulty.All,
        limit: Int = 20,
    ): List<LearningRouteSummaryUiModel>

    suspend fun getRouteDetail(
        routeId: String,
        limit: Int = 10,
        includeAi: Boolean = true,
    ): LearningRouteDetailUiModel

    suspend fun buildRoute(
        seedType: LearningRouteSeedType,
        seedKey: String,
        difficulty: LearningRouteDifficulty = LearningRouteDifficulty.Beginner,
        limit: Int = 8,
    ): LearningRouteDetailUiModel

    suspend fun getNextStep(
        routeId: String,
        completedStepIds: List<String> = emptyList(),
    ): LearningRouteNextUiModel
}

class DefaultLearningRoutesRepository @Inject constructor(
    private val api: LearningRoutesApi,
    private val profileRepository: LocalProfileRepository,
) : LearningRoutesRepository {

    override suspend fun getRoutes(
        difficulty: LearningRouteDifficulty,
        limit: Int,
    ): List<LearningRouteSummaryUiModel> =
        api.getLearningRoutes(
            LearningRoutesListQuery(difficulty = difficulty, limit = limit),
        ).map { it.toSummaryUiModel() }

    override suspend fun getRouteDetail(
        routeId: String,
        limit: Int,
        includeAi: Boolean,
    ): LearningRouteDetailUiModel =
        api.getLearningRouteDetail(
            LearningRouteDetailQuery(routeId = routeId, limit = limit, includeAi = includeAi),
        ).toDetailUiModel()

    override suspend fun buildRoute(
        seedType: LearningRouteSeedType,
        seedKey: String,
        difficulty: LearningRouteDifficulty,
        limit: Int,
    ): LearningRouteDetailUiModel =
        api.buildLearningRoute(
            LearningRouteBuildQuery(
                seedType = seedType,
                seedKey = seedKey,
                difficulty = difficulty,
                limit = limit,
            ),
        ).toDetailUiModel()

    override suspend fun getNextStep(
        routeId: String,
        completedStepIds: List<String>,
    ): LearningRouteNextUiModel =
        api.getLearningRouteNextStep(
            LearningRouteNextQuery(
                routeId = routeId,
                completedStepIds = completedStepIds,
                profileId = profileRepository.currentProfileId(),
            ),
        ).toNextUiModel()
}

internal fun LearningRouteSummaryDto.toSummaryUiModel(): LearningRouteSummaryUiModel =
    LearningRouteSummaryUiModel(
        routeId = routeId,
        title = title.orEmpty(),
        subtitle = subtitle,
        description = description,
        difficulty = difficulty,
        estimatedMinutes = estimatedMinutes,
        stepCount = stepCount,
        tags = tags,
        coverImageUrl = coverImageUrl,
    )

internal fun LearningRouteDetailDto.toDetailUiModel(): LearningRouteDetailUiModel =
    LearningRouteDetailUiModel(
        routeId = routeId,
        title = title.orEmpty(),
        description = description,
        difficulty = difficulty,
        estimatedMinutes = estimatedMinutes,
        sections = sections.map { it.toSectionUiModel() },
        steps = steps.map { it.toStepUiModel() }.sortedBy { it.order },
        relatedRoutes = relatedRoutes.map { it.toSummaryUiModel() },
    )

internal fun LearningRouteNextDto.toNextUiModel(): LearningRouteNextUiModel =
    LearningRouteNextUiModel(
        routeId = routeId,
        completed = completed,
        nextStep = nextStep?.toStepUiModel(),
        relatedRoutes = relatedRoutes.map { it.toSummaryUiModel() },
    )

internal fun com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSectionDto.toSectionUiModel(): LearningRouteSectionUiModel =
    LearningRouteSectionUiModel(
        sectionId = sectionId,
        title = title.orEmpty(),
        description = description,
        stepIds = stepIds,
    )

internal fun LearningRouteStepDto.toStepUiModel(): LearningRouteStepUiModel =
    LearningRouteStepUiModel(
        stepId = stepId,
        order = order,
        title = title.orEmpty(),
        description = description,
        targetType = targetType,
        targetId = targetId,
        reason = reason,
        estimatedMinutes = estimatedMinutes,
        required = required,
    )
