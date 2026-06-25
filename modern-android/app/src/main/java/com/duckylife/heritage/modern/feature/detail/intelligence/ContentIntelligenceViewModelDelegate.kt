package com.duckylife.heritage.modern.feature.detail.intelligence

import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRepository
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.toUiError
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 详情页智能内容层的 ViewModel 委托。
 *
 * 三个详情 ViewModel 通过该委托共享增强请求、错误降级、重试与取消逻辑。
 */
interface ContentIntelligenceViewModelDelegate {
    val uiState: StateFlow<ContentIntelligenceUiState>

    /**
     * 加载指定内容的 V3 content page。若 [ref] 与当前相同，不会重复发起请求。
     */
    fun load(
        ref: ContentIntelligenceRef,
        onPageLoaded: (ContentIntelligencePage) -> Unit = {},
        onFallbackRequired: () -> Unit = {},
    )

    /**
     * 重试最后一次请求的 ref。
     */
    fun retry()
}

/**
 * 用于向 ViewModel 提供绑定其 [CoroutineScope] 的委托实例。
 */
interface ContentIntelligenceViewModelDelegateFactory {
    fun create(scope: CoroutineScope): ContentIntelligenceViewModelDelegate
}

class DefaultContentIntelligenceViewModelDelegateFactory @Inject constructor(
    private val repository: ContentIntelligenceRepository,
) : ContentIntelligenceViewModelDelegateFactory {
    override fun create(scope: CoroutineScope): ContentIntelligenceViewModelDelegate =
        DefaultContentIntelligenceViewModelDelegate(scope, repository)
}

internal class DefaultContentIntelligenceViewModelDelegate(
    private val scope: CoroutineScope,
    private val repository: ContentIntelligenceRepository,
) : ContentIntelligenceViewModelDelegate {

    private val _uiState = MutableStateFlow(ContentIntelligenceUiState())
    override val uiState: StateFlow<ContentIntelligenceUiState> = _uiState.asStateFlow()

    private var job: Job? = null
    private var lastRef: ContentIntelligenceRef? = null
    private var onPageLoaded: (ContentIntelligencePage) -> Unit = {}
    private var onFallbackRequired: () -> Unit = {}

    override fun load(
        ref: ContentIntelligenceRef,
        onPageLoaded: (ContentIntelligencePage) -> Unit,
        onFallbackRequired: () -> Unit,
    ) {
        if (lastRef == ref) return
        lastRef = ref
        this.onPageLoaded = onPageLoaded
        this.onFallbackRequired = onFallbackRequired
        startLoad(ref)
    }

    override fun retry() {
        val ref = lastRef ?: return
        startLoad(ref)
    }

    private fun startLoad(ref: ContentIntelligenceRef) {
        job?.cancel()
        // 切换详情时不能沿用上一条内容的 action/AI 卡片；否则用户在新详情的
        // loading 阶段可能跳转到旧内容对应的图谱或相似内容。
        _uiState.value = ContentIntelligenceUiState(isLoading = true)
        job = scope.launch {
            runCatchingCancellable { repository.loadContentPage(ref) }
                .onSuccess { page ->
                    // repository/HTTP 实现不一定立刻响应 cancel；只接受当前 ref 的结果。
                    if (lastRef != ref) return@onSuccess
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            page = page,
                            aiSection = page.aiSection,
                            graphSection = page.graphSection,
                            recommendationSection = page.recommendationSection,
                            digestSection = page.digestSection,
                            learningRoutesAvailable = page.learningRoutesAvailable,
                            warnings = page.warnings,
                        )
                    }
                    onPageLoaded(page)
                }
                .onFailure { throwable ->
                    if (lastRef == ref) {
                        handleFailure(throwable)
                    }
                }
        }
    }

    private fun handleFailure(throwable: Throwable) {
        val isBadRequest = throwable is ResponseException &&
            throwable.response.status == HttpStatusCode.BadRequest
        if (isBadRequest) {
            // 400 通常是来源路由尚未解析为 ObjectId 或缺少 profileId；按需求跳过增强层。
            _uiState.update {
                it.copy(
                    isLoading = false,
                    loadError = null,
                )
            }
            onFallbackRequired()
            return
        }

        val errorKind = throwable.toUiError().kind
        _uiState.update {
            it.copy(
                isLoading = false,
                loadError = when (errorKind) {
                    // 503 / 网络错误只降级为板块不可用，不作为整页 fatal error。
                    ErrorKind.ServerError,
                    ErrorKind.NetworkUnavailable,
                    ErrorKind.Timeout,
                    -> null

                    else -> errorKind
                },
                aiSection = IntelligenceSection<AiCardDto>(SectionStatus.Unavailable),
                graphSection = IntelligenceSection<GraphNeighborsDto>(SectionStatus.Unavailable),
                recommendationSection = IntelligenceSection<List<ContentRefDto>>(SectionStatus.Unavailable),
                digestSection = IntelligenceSection<ContentDigestSectionDto>(SectionStatus.Unavailable),
                learningRoutesAvailable = false,
            )
        }
        onFallbackRequired()
    }
}
