package com.duckylife.heritage.modern.core.profile

import kotlinx.coroutines.flow.Flow

/**
 * 本地匿名 profile 仓库。
 *
 * 一个安装实例只对应一个 profile ID。首次访问时生成并持久化，之后稳定读取。
 */
interface LocalProfileRepository {
    /**
     * 当前 profile ID 的实时流。
     */
    val profileId: Flow<String>

    /**
     * 返回当前 profile ID。若尚未持久化，则生成新 ID 写入 DataStore 后返回。
     *
     * 该方法可在 suspend 上下文中安全调用，也适用于 Ktor 请求插件等需要 suspend 的场景。
     */
    suspend fun currentProfileId(): String
}
