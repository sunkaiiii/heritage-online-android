package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.DirectoryDetailEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString

fun DirectoryItemDetailDto.toEntity(
    kind: DirectoryItemKind,
    sourceId: String?,
    updatedAtEpochMillis: Long,
): DirectoryDetailEntity =
    DirectoryDetailEntity(
        id = id ?: sourceId ?: sourceUrl ?: title.orEmpty(),
        sourceId = sourceId,
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
        nominationType = nominationType,
        protectionUnit = protectionUnit,
        galleryJson = HeritageJson.encodeToString(gallery),
        contentBlocksJson = HeritageJson.encodeToString(contentBlocks),
        relatedProjectsJson = HeritageJson.encodeToString(relatedProjects),
        relatedInheritorsJson = HeritageJson.encodeToString(relatedInheritors),
        relatedDocumentsJson = HeritageJson.encodeToString(relatedDocuments),
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

fun DirectoryDetailEntity.toDto(): DirectoryItemDetailDto =
    DirectoryItemDetailDto(
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
        nominationType = nominationType,
        protectionUnit = protectionUnit,
        gallery = galleryJson.decodeJsonOrNull<List<MediaAssetDto>>(HeritageJson).orEmpty(),
        contentBlocks = contentBlocksJson.decodeJsonOrNull<List<ArticleContentBlockDto>>(HeritageJson).orEmpty(),
        relatedProjects = relatedProjectsJson.decodeJsonOrNull<List<DirectoryReferenceDto>>(HeritageJson).orEmpty(),
        relatedInheritors = relatedInheritorsJson.decodeJsonOrNull<List<DirectoryReferenceDto>>(HeritageJson).orEmpty(),
        relatedDocuments = relatedDocumentsJson.decodeJsonOrNull<List<DirectoryReferenceDto>>(HeritageJson).orEmpty(),
    )
