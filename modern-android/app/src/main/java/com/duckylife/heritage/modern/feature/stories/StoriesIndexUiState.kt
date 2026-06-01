package com.duckylife.heritage.modern.feature.stories

import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class StoriesIndexUiState(
    val isLoading: Boolean = true,
    val errorKind: ErrorKind? = null,
    val regions: List<TaxonomyTopicDto> = emptyList(),
    val categories: List<TaxonomyTopicDto> = emptyList(),
    val years: List<TimelineYearBucketDto> = emptyList(),
)
