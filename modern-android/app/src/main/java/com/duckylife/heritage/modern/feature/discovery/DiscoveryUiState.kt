package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTodayDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTrendingDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryWeeklyDto
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.FeaturedCollectionDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto

data class DiscoveryClassicData(
    val exploreIndex: ExploreIndexDto? = null,
    val topics: List<ExploreTopicInfoDto> = emptyList(),
    val learningPaths: List<LearningPathDto> = emptyList(),
    val featuredCollections: List<FeaturedCollectionDto> = emptyList(),
    val regionAtlas: RegionAtlasDto? = null,
)

data class DiscoveryUiState(
    // Discovery v2 sections (high priority)
    val today: DiscoverySectionState<DiscoveryTodayDto> = DiscoverySectionState(),
    val trending: DiscoverySectionState<DiscoveryTrendingDto> = DiscoverySectionState(),
    val weekly: DiscoverySectionState<DiscoveryWeeklyDto> = DiscoverySectionState(),
    // Classic sections (lower priority)
    val classic: DiscoverySectionState<DiscoveryClassicData> = DiscoverySectionState(),
    // Serendipity
    val serendipityItem: DiscoveryItemDto? = null,
    val serendipityLoading: Boolean = false,
) {
    val isAnyLoading: Boolean
        get() = today.isLoading || trending.isLoading || weekly.isLoading || classic.isLoading
    val isAllFailed: Boolean
        get() = today.hasError && trending.hasError && weekly.hasError && classic.hasError
}
