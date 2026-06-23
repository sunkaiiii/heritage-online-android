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

/**
 * 基于 DataStore 的 [LocalProfileRepository] 实现。
 *
 * 首次访问时生成 `android_<UUID>` 并写入 DataStore；后续调用返回同一个 ID。
 * 写入与读取在同一个 [edit] 事务中完成，避免并发请求产生多个不同 ID。
 */
@Singleton
class DataStoreLocalProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : LocalProfileRepository {

    override val profileId: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[ProfileIdKey] ?: generateProfileId()
        }
        .distinctUntilChanged()

    override suspend fun currentProfileId(): String {
        return dataStore.edit { preferences ->
            val existing = preferences[ProfileIdKey]
            if (existing == null || !existing.isValidProfileId()) {
                val generated = generateProfileId()
                preferences[ProfileIdKey] = generated
            }
        }[ProfileIdKey] ?: generateProfileId()
    }

    private companion object {
        val ProfileIdKey = stringPreferencesKey("heritage_profile_id")
    }
}
