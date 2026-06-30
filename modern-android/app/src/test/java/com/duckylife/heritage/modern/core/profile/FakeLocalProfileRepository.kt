package com.duckylife.heritage.modern.core.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 用于测试的固定 profile ID 仓库。
 *
 * 默认 ID 为 `android_test_profile`，可通过构造函数传入其他值。
 */
class FakeLocalProfileRepository(
    private val id: String = "android_test_profile",
) : LocalProfileRepository {
    private val _profileId = MutableStateFlow(id)

    override val profileId: Flow<String> = _profileId.asStateFlow()

    override suspend fun currentProfileId(): String = _profileId.value
}
