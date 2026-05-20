package com.duckylife.heritage.modern.ui.preview

import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto

fun MediaAssetDto.previewUrl(): String? =
    displayUrl ?: thumbnailUrl ?: originalUrl ?: sourceUrl
