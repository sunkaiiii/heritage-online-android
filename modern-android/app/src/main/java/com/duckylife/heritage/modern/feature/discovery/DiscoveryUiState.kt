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
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class DiscoveryUiState(
    val isLoading: Boolean = false,
    val errorKind: ErrorKind? = null,
    val exploreIndex: ExploreIndexDto? = null,
    val topics: List<ExploreTopicInfoDto> = emptyList(),
    val learningPaths: List<LearningPathDto> = emptyList(),
    val featuredCollections: List<FeaturedCollectionDto> = emptyList(),
    val regionAtlas: RegionAtlasDto? = null,
    // Discovery v2
    val today: DiscoveryTodayDto? = null,
    val trending: DiscoveryTrendingDto? = null,
    val weekly: DiscoveryWeeklyDto? = null,
    val serendipityItem: DiscoveryItemDto? = null,
    val serendipityLoading: Boolean = false,
)
