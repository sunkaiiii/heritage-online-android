package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import java.net.URI
import java.util.logging.Logger

/**
 * 统一的后端内容/图片 URL 解析器。
 *
 * 后端可能返回完整公开 URL、根相对 URL（`/images/...`）或历史 `localhost` URL。
 * 本解析器负责把它们转换为客户端可直接请求（Coil、浏览器等）的绝对 URL，
 * 并把指向手机自身 localhost 的地址视为不可达。
 *
 * 解析规则：
 * 1. `null`、空串、纯空白 -> `null`。
 * 2. 已是 `http://` 或 `https://` 的绝对 URL -> 原样返回；但 host 为 localhost/127.0.0.1/::1 时返回 `null`。
 * 3. 以 `/` 开始的相对 URL -> 取 API base URL 的 scheme + authority，再拼接路径。
 * 4. 不以 `/` 开始的相对 URL -> 按 API 根目录 resolve。
 *
 * @param baseUrl API 根地址，例如 `https://tuantuan.myds.me:28887` 或 `https://example.com/api`。
 */
class HeritageUrlResolver(
    baseUrl: String,
) {
    private val normalizedBase = baseUrl.trimEnd('/')
    private val baseUri by lazy { URI(normalizedBase) }

    /**
     * 把原始 URL 字符串解析为可直接加载的绝对 URL。
     */
    fun resolve(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        val trimmed = rawUrl.trim()
        if (trimmed.isLocalhostReference()) {
            logLocalhostSkipped(trimmed)
            return null
        }

        val lower = trimmed.lowercase()
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            val host = try {
                URI(trimmed).host
            } catch (_: Exception) {
                null
            }
            if (host.isLocalhostHost()) {
                logLocalhostSkipped(trimmed)
                return null
            }
            return trimmed
        }

        return if (trimmed.startsWith("/")) {
            val authority = baseUri.authority ?: return null
            "${baseUri.scheme}://$authority$trimmed"
        } else {
            "$normalizedBase/$trimmed"
        }
    }

    private fun logLocalhostSkipped(url: String) {
        Logger.getLogger(TAG).fine { "Skipped localhost image URL: $url" }
    }

    private companion object {
        const val TAG = "HeritageUrlResolver"
    }
}

private fun String.isLocalhostReference(): Boolean {
    val lower = lowercase()
    return startsWith("http://localhost") ||
        startsWith("https://localhost") ||
        startsWith("http://127.0.0.1") ||
        startsWith("https://127.0.0.1") ||
        startsWith("http://[::1]") ||
        startsWith("https://[::1]")
}

private fun String?.isLocalhostHost(): Boolean {
    if (this == null) return false
    return equals("localhost", ignoreCase = true) ||
        equals("127.0.0.1") ||
        equals("::1")
}

/**
 * 从候选 URL 中挑选最佳图片 URL，并用 [resolver] 解析为绝对 URL。
 */
fun MediaAssetDto?.resolvedBestUrl(resolver: HeritageUrlResolver): String? {
    val candidate = this?.let { displayUrl ?: thumbnailUrl ?: originalUrl ?: sourceUrl }
    return resolver.resolve(candidate)
}

/**
 * 从候选 URL 中挑选适合预览/全屏查看的图片 URL，并用 [resolver] 解析为绝对 URL。
 */
fun MediaAssetDto?.resolvedPreviewUrl(resolver: HeritageUrlResolver): String? {
    val candidate = this?.let { displayUrl ?: thumbnailUrl ?: originalUrl ?: sourceUrl }
    return resolver.resolve(candidate)
}
