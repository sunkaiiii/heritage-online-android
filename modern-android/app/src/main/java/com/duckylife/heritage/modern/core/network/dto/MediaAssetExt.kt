package com.duckylife.heritage.modern.core.network.dto

import com.duckylife.heritage.modern.core.network.HeritageJson

/**
 * 从已序列化的 [MediaAssetDto] JSON 中选取最佳封面/缩略图 URL。
 *
 * 仅执行一次反序列化，优先使用 displayUrl，其次 thumbnailUrl。
 */
fun extractCoverImageUrl(coverImageJson: String?): String? {
    if (coverImageJson.isNullOrBlank()) return null
    return runCatching {
        HeritageJson.decodeFromString<MediaAssetDto>(coverImageJson)
    }.getOrNull()?.let { asset ->
        asset.displayUrl ?: asset.thumbnailUrl
    }
}
