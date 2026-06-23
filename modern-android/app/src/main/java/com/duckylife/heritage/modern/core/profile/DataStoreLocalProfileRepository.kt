package com.duckylife.heritage.modern.core.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 基于 DataStore 的 [LocalProfileRepository] 实现。
 *
 * 首次访问时生成 `android_<UUID>` 并写入 DataStore；后续调用返回同一个 ID。
 * 为避免每个 HTTP 请求都获取 DataStore 互斥锁，ID 一旦生成即缓存在内存中。
 */
@Singleton
class DataStoreLocalProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : LocalProfileRepository {

    @Volatile
    private var cachedProfileId: String? = null

    private val cacheLock = Mutex()

    override val profileId: Flow<String> = dataStore.data
        .map { preferences ->
            cachedProfileId
                ?: preferences[ProfileIdKey]
                ?: currentProfileId()
        }
        .distinctUntilChanged()

    override suspend fun currentProfileId(): String {
        cachedProfileId?.let { return it }
        return cacheLock.withLock {
            cachedProfileId?.let { return it }
            val value = loadOrGenerateProfileId()
            cachedProfileId = value
            value
        }
    }

    private suspend fun loadOrGenerateProfileId(): String {
        return try {
            val persisted = dataStore.edit { preferences ->
                val existing = preferences[ProfileIdKey]
                if (existing == null || !existing.isValidProfileId()) {
                    val generated = generateProfileId()
                    preferences[ProfileIdKey] = generated
                }
            }[ProfileIdKey]
            // 如果 DataStore 写入成功但读取失败，仍使用本次生成的 ID 以保持会话内稳定。
            persisted?.takeIf { it.isValidProfileId() } ?: generateProfileId()
        } catch (_: Throwable) {
            // DataStore 损坏时回退到内存 ID；缓存保证同一会话内不变。
            generateProfileId()
        }
    }

    private companion object {
        val ProfileIdKey = stringPreferencesKey("heritage_profile_id")
    }
}
