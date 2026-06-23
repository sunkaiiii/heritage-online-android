package com.duckylife.heritage.modern.core.profile

import java.util.UUID

/**
 * 本机匿名用户档案。
 *
 * 后端通过 `X-Heritage-Profile-Id` 请求头区分用户，不使用账号、OAuth 或密码。
 * 首次安装时生成一次 profile ID，格式为 `android_` 加 UUID 去掉大括号后的字符串，
 * 例如 `android_550e8400-e29b-41d4-a716-446655440000`。
 *
 * 长度小于 64，且只包含字母、数字、`_`、`-`，符合后端白名单 `^[a-zA-Z0-9_-]{1,64}$`。
 */
data class LocalProfile(
    val profileId: String,
)

private val ProfileIdRegex = Regex("^[a-zA-Z0-9_-]{1,64}$")

fun generateProfileId(): String = "android_${UUID.randomUUID()}".also { id ->
    require(id.matches(ProfileIdRegex)) { "Generated profile ID does not match backend whitelist: $id" }
}

fun String.isValidProfileId(): Boolean = matches(ProfileIdRegex)
