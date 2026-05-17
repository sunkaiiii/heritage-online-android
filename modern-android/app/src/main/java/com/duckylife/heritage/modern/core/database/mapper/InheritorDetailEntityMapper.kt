package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.InheritorDetailEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString

fun InheritorDetailDto.toEntity(
    sourceId: String?,
    updatedAtEpochMillis: Long,
): InheritorDetailEntity =
    InheritorDetailEntity(
        id = id ?: sourceId ?: sourceUrl ?: name.orEmpty(),
        sourceId = sourceId,
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
        contentBlocksJson = HeritageJson.encodeToString(contentBlocks),
        relatedProjectsJson = HeritageJson.encodeToString(relatedProjects),
        relatedInheritorsJson = HeritageJson.encodeToString(relatedInheritors),
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

fun InheritorDetailEntity.toDto(): InheritorDetailDto =
    InheritorDetailDto(
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
        contentBlocks = contentBlocksJson.decodeJsonOrNull<List<ArticleContentBlockDto>>(HeritageJson).orEmpty(),
        relatedProjects = relatedProjectsJson.decodeJsonOrNull<List<DirectoryReferenceDto>>(HeritageJson).orEmpty(),
        relatedInheritors = relatedInheritorsJson.decodeJsonOrNull<List<DirectoryReferenceDto>>(HeritageJson).orEmpty(),
    )
