package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.ArticleEntity
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun ArticleQuery.queryKey(): String =
    listOf(
        category.wireName,
        year?.toString().orEmpty(),
        keywords.orEmpty(),
    ).joinToString(separator = "|")

fun ArticleSummaryDto.toEntity(
    query: ArticleQuery,
    page: Int,
    positionInPage: Int,
): ArticleEntity =
    ArticleEntity(
        id = id ?: sourceUrl ?: "${query.queryKey()}-$page-$positionInPage",
        queryKey = query.queryKey(),
        category = category.wireName,
        title = title,
        summary = summary,
        publishedAt = publishedAt,
        coverImageJson = coverImage?.let { HeritageJson.encodeToString(it) },
        sourceUrl = sourceUrl,
        page = page,
        positionInPage = positionInPage,
    )

fun ArticleEntity.toDto(): ArticleSummaryDto =
    ArticleSummaryDto(
        id = id,
        category = ArticleCategory.entries.firstOrNull { it.wireName == category } ?: ArticleCategory.News,
        title = title,
        summary = summary,
        publishedAt = publishedAt,
        coverImage = coverImageJson?.decodeJsonOrNull<MediaAssetDto>(HeritageJson),
        sourceUrl = sourceUrl,
    )

inline fun <reified T> String.decodeJsonOrNull(json: Json): T? =
    runCatching { json.decodeFromString<T>(this) }.getOrNull()
