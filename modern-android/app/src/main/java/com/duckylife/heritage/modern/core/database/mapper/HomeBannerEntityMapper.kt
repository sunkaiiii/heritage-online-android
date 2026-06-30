package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.HomeBannerEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString

fun HomeBannerDto.toEntity(updatedAtEpochMillis: Long) = HomeBannerEntity(
    id = id ?: targetUrl ?: sortOrder.toString(),
    sortOrder = sortOrder,
    targetUrl = targetUrl,
    displayImageJson = displayImage?.let { HeritageJson.encodeToString(it) },
    mobileImageJson = mobileImage?.let { HeritageJson.encodeToString(it) },
    desktopImageJson = desktopImage?.let { HeritageJson.encodeToString(it) },
    updatedAtEpochMillis = updatedAtEpochMillis,
)

fun HomeBannerEntity.toDto() = HomeBannerDto(
    id = id,
    sortOrder = sortOrder,
    targetUrl = targetUrl,
    displayImage = displayImageJson?.decodeBannerJsonOrNull(),
    mobileImage = mobileImageJson?.decodeBannerJsonOrNull(),
    desktopImage = desktopImageJson?.decodeBannerJsonOrNull(),
)

private inline fun <reified T> String.decodeBannerJsonOrNull(): T? =
    runCatching { HeritageJson.decodeFromString<T>(this) }.getOrNull()
