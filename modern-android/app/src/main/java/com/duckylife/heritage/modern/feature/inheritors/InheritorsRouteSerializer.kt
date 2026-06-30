package com.duckylife.heritage.modern.feature.inheritors

import com.duckylife.heritage.modern.core.network.HeritageJson

internal fun serializeInheritorsRoutes(stack: List<InheritorsRouteKey>): String =
    try {
        HeritageJson.encodeToString(stack.map { it.toRouteState() })
    } catch (_: Exception) {
        // 降级：返回空 JSON 数组，反序列化后回到列表页
        "[]"
    }

internal fun deserializeInheritorsRoutes(value: String): List<InheritorsRouteKey> =
    try {
        if (value.isBlank()) listOf(InheritorsRouteKey.InheritorsList)
        else {
            val states = HeritageJson.decodeFromString<List<InheritorsRouteState>>(value)
            if (states.isEmpty()) listOf(InheritorsRouteKey.InheritorsList)
            else states.map { it.toRouteKey() }
        }
    } catch (_: Exception) {
        listOf(InheritorsRouteKey.InheritorsList)
    }
