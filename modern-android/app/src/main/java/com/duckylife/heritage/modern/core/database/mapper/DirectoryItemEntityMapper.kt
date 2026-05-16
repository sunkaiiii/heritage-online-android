package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString

fun DirectoryItemQuery.queryKey(): String =
    listOf(
        kind.wireName,
        keywords.orEmpty(),
        region.orEmpty(),
        category.orEmpty(),
        year?.toString().orEmpty(),
        listType.orEmpty(),
    ).joinToString(separator = "|")

fun DirectoryItemSummaryDto.toEntity(
    query: DirectoryItemQuery,
    page: Int,
    positionInPage: Int,
): DirectoryItemEntity =
    DirectoryItemEntity(
        id = id ?: sourceUrl ?: "${query.queryKey()}-$page-$positionInPage",
        queryKey = query.queryKey(),
        kind = kind.wireName,
        title = title,
        summary = summary,
        category = category,
        region = region,
        projectCode = projectCode,
        batch = batch,
        publishedYear = publishedYear,
        listType = listType,
        coverImageJson = coverImage?.let { HeritageJson.encodeToString(it) },
        sourceUrl = sourceUrl,
        page = page,
        positionInPage = positionInPage,
    )

fun DirectoryItemEntity.toDto(): DirectoryItemSummaryDto =
    DirectoryItemSummaryDto(
        id = id,
        kind = DirectoryItemKind.entries.firstOrNull { it.wireName == kind } ?: DirectoryItemKind.NationalProject,
        title = title,
        summary = summary,
        category = category,
        region = region,
        projectCode = projectCode,
        batch = batch,
        publishedYear = publishedYear,
        listType = listType,
        coverImage = coverImageJson?.decodeJsonOrNull<MediaAssetDto>(HeritageJson),
        sourceUrl = sourceUrl,
    )
