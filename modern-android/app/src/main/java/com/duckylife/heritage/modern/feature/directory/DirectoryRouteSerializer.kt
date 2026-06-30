package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.network.HeritageJson

internal fun serializeDirectoryRoutes(stack: List<DirectoryRouteKey>): String =
    try {
        HeritageJson.encodeToString(stack.map { it.toRouteState() })
    } catch (_: Exception) {
        // 降级：返回空 JSON 数组，反序列化后回到列表页
        "[]"
    }

internal fun deserializeDirectoryRoutes(value: String): List<DirectoryRouteKey> =
    try {
        if (value.isBlank()) listOf(DirectoryRouteKey.DirectoryList)
        else {
            val states = HeritageJson.decodeFromString<List<DirectoryRouteState>>(value)
            if (states.isEmpty()) listOf(DirectoryRouteKey.DirectoryList)
            else states.map { it.toRouteKey() }
        }
    } catch (_: Exception) {
        listOf(DirectoryRouteKey.DirectoryList)
    }
