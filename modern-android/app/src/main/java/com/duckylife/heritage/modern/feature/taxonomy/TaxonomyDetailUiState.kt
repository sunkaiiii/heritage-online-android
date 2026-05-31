package com.duckylife.heritage.modern.feature.taxonomy

import com.duckylife.heritage.modern.core.network.dto.TaxonomyCategoryDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyRegionDetailDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class TaxonomyDetailUiState(
    val isLoading: Boolean = true,
    val errorKind: ErrorKind? = null,
    val categoryDetail: TaxonomyCategoryDetailDto? = null,
    val regionDetail: TaxonomyRegionDetailDto? = null,
)
