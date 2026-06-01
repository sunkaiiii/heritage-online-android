package com.duckylife.heritage.modern.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ReadingPathContentRef
import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.data.ReadingPathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 阅读路径记录器 ViewModel。
 *
 * 详情页可以注入此 ViewModel 来记录跨内容跳转。
 * 记录在 viewModelScope 中异步完成，不阻断页面跳转。
 */
@HiltViewModel
class ReadingPathRecorderViewModel @Inject constructor(
    private val repository: ReadingPathRepository,
) : ViewModel() {

    /**
     * 记录一次从 [from] 到 [to] 的阅读路径事件。
     *
     * @param from 来源内容引用
     * @param to 目标内容引用
     * @param source 跳转来源区块（如 blendedRecommendation、related、collection 等）
     */
    fun record(from: ReadingPathContentRef, to: ReadingPathContentRef, source: String) {
        viewModelScope.launch {
            try {
                // 生成稳定 id：同一条路径再次发生时更新 createdAt
                val id = "${from.type}:${from.id}->${to.type}:${to.id}:$source"
                val event = ReadingPathEvent(
                    id = id,
                    fromType = from.type,
                    fromId = from.id,
                    fromTitle = from.title,
                    toType = to.type,
                    toId = to.id,
                    toTitle = to.title,
                    source = source,
                    toCategory = to.category,
                    toKind = to.kind,
                    toSourceId = to.sourceId,
                    toSourceUrl = to.sourceUrl,
                    toSubtitle = to.subtitle,
                    toImageUrl = to.imageUrl,
                )
                repository.record(event)
            } catch (_: Exception) {
                // 记录失败不阻断页面跳转
            }
        }
    }
}
