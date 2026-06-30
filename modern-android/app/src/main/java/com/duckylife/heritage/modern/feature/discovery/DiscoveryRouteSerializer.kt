package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.network.HeritageJson

internal fun serializeDiscoveryRoutes(stack: List<DiscoveryRouteKey>): String =
    try {
        HeritageJson.encodeToString(stack.map { it.toRouteState() })
    } catch (_: Exception) {
        // 降级：返回空 JSON 数组，反序列化后回到首页
        "[]"
    }

internal fun deserializeDiscoveryRoutes(value: String): List<DiscoveryRouteKey> =
    try {
        if (value.isBlank()) listOf(DiscoveryRouteKey.DiscoveryIndex)
        else {
            val states = HeritageJson.decodeFromString<List<RouteState>>(value)
            if (states.isEmpty()) listOf(DiscoveryRouteKey.DiscoveryIndex)
            else states.map { it.toRouteKey() }
        }
    } catch (_: Exception) {
        listOf(DiscoveryRouteKey.DiscoveryIndex)
    }
