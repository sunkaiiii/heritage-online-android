package com.duckylife.heritage.modern.ui.preview

import com.duckylife.heritage.modern.core.network.HeritageUrlResolver
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.core.network.previewImageUrl

/**
 * 挑选适合预览/全屏查看的图片候选 URL（不做 base URL 解析）。
 */
fun MediaAssetDto.previewUrl(): String? = previewImageUrl()

/**
 * 挑选适合预览/全屏查看的图片候选 URL，并用 [resolver] 解析为绝对 URL。
 */
fun MediaAssetDto.resolvedPreviewUrl(resolver: HeritageUrlResolver): String? {
    return resolver.resolve(previewImageUrl())
}
