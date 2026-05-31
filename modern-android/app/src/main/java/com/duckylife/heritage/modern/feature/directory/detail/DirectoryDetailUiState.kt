package com.duckylife.heritage.modern.feature.directory.detail

import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class DirectoryDetailUiState(
    val isLoading: Boolean = true,
    val item: DirectoryItemDetailDto? = null,
    val errorKind: ErrorKind? = null,
    val isFavorite: Boolean = false,
    val isContentStale: Boolean = false,
    val contextLoading: Boolean = false,
    val context: DetailContextDto? = null,
    val contextErrorKind: ErrorKind? = null,
    // Content Digest
    val digest: ContentDigestDto? = null,
    val digestLoading: Boolean = false,
    val digestErrorKind: ErrorKind? = null,
    // Blended Recommendations
    val blendedRecommendations: BlendedRecommendationResponseDto? = null,
    val blendedLoading: Boolean = false,
)
