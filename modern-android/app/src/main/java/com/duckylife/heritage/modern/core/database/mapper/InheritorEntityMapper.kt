package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.InheritorEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString

fun InheritorQuery.queryKey(): String =
    listOf(
        keywords.orEmpty(),
        region.orEmpty(),
        category.orEmpty(),
        year?.toString().orEmpty(),
        gender.orEmpty(),
    ).joinToString(separator = "|")

fun InheritorSummaryDto.toEntity(
    query: InheritorQuery,
    page: Int,
    positionInPage: Int,
): InheritorEntity =
    InheritorEntity(
        id = id ?: sourceUrl ?: "${query.queryKey()}-$page-$positionInPage",
        queryKey = query.queryKey(),
        name = name,
        gender = gender,
        birthDateText = birthDateText,
        ethnicity = ethnicity,
        category = category,
        projectCode = projectCode,
        projectName = projectName,
        region = region,
        batch = batch,
        description = description,
        coverImageJson = coverImage?.let { HeritageJson.encodeToString(it) },
        sourceUrl = sourceUrl,
        page = page,
        positionInPage = positionInPage,
    )

fun InheritorEntity.toDto(): InheritorSummaryDto =
    InheritorSummaryDto(
        id = id,
        name = name,
        gender = gender,
        birthDateText = birthDateText,
        ethnicity = ethnicity,
        category = category,
        projectCode = projectCode,
        projectName = projectName,
        region = region,
        batch = batch,
        description = description,
        coverImage = coverImageJson?.decodeJsonOrNull<MediaAssetDto>(HeritageJson),
        sourceUrl = sourceUrl,
    )
