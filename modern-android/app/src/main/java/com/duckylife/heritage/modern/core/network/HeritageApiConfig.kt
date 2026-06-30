package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.BuildConfig

/**
 * 远程 API 配置。
 *
 * @param baseUrl API 根地址，格式为 `https://host[:port][/optional-prefix]`，内部使用时会 `trimEnd('/')`。
 * @param trustSelfSignedCertificates 是否信任自签名证书。Release 构建中必须为 `false`。
 * @param requireHttpsForRelease Release 构建是否强制要求 `https://` 基地址。默认与 `BuildConfig.DEBUG` 相反。
 */
data class HeritageApiConfig(
    val baseUrl: String,
    val trustSelfSignedCertificates: Boolean,
    val requireHttpsForRelease: Boolean = !BuildConfig.DEBUG,
) {
    init {
        if (requireHttpsForRelease) {
            require(baseUrl.startsWith("https://", ignoreCase = true)) {
                "Release builds require an HTTPS API base URL, but was: $baseUrl"
            }
            require(!trustSelfSignedCertificates) {
                "Release builds cannot trust self-signed certificates. " +
                    "Set heritageTrustSelfSigned=false or use a publicly trusted certificate."
            }
        }
    }
}
