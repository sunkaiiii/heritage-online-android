package com.duckylife.heritage.modern.ui.preview

import com.duckylife.heritage.modern.core.network.HeritageUrlResolver
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto

fun buildPreviewUrls(
    resolver: HeritageUrlResolver,
    coverImage: MediaAssetDto? = null,
    gallery: List<MediaAssetDto> = emptyList(),
    contentBlocks: List<ArticleContentBlockDto> = emptyList(),
): List<String> = buildList {
    coverImage?.resolvedPreviewUrl(resolver)?.let { add(it) }
    gallery.forEach { img -> img.resolvedPreviewUrl(resolver)?.let { add(it) } }
    contentBlocks.forEach { block ->
        if (block.type == ArticleContentBlockType.Image) {
            block.image?.resolvedPreviewUrl(resolver)?.let { add(it) }
        }
    }
}
