package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.ArticleDetailEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString

fun ArticleDetailDto.toEntity(
    category: ArticleCategory,
    sourceId: String?,
    sourceUrl: String?,
    updatedAtEpochMillis: Long,
): ArticleDetailEntity =
    ArticleDetailEntity(
        id = id ?: sourceId ?: sourceUrl ?: this.sourceUrl ?: title.orEmpty(),
        sourceId = sourceId,
        category = category.wireName,
        title = title,
        summary = summary,
        publishedAt = publishedAt,
        coverImageJson = coverImage?.let { HeritageJson.encodeToString(it) },
        sourceUrl = this.sourceUrl ?: sourceUrl,
        sourceName = sourceName,
        author = author,
        editor = editor,
        contentBlocksJson = HeritageJson.encodeToString(contentBlocks),
        relatedArticlesJson = HeritageJson.encodeToString(relatedArticles),
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

fun ArticleDetailEntity.toDto(): ArticleDetailDto =
    ArticleDetailDto(
        id = id,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
        title = title,
        summary = summary,
        publishedAt = publishedAt,
        coverImage = coverImageJson?.decodeJsonOrNull<MediaAssetDto>(HeritageJson),
        sourceUrl = sourceUrl,
        sourceName = sourceName,
        author = author,
        editor = editor,
        contentBlocks = contentBlocksJson.decodeJsonOrNull<List<ArticleContentBlockDto>>(HeritageJson).orEmpty(),
        relatedArticles = relatedArticlesJson.decodeJsonOrNull<List<ArticleReferenceDto>>(HeritageJson).orEmpty(),
    )
