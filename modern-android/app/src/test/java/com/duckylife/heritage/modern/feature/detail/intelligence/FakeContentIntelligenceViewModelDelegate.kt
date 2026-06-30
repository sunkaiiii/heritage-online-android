package com.duckylife.heritage.modern.feature.detail.intelligence

import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 测试用 ViewModel 委托工厂，返回一个默认空状态的 delegate。
 */
class FakeContentIntelligenceViewModelDelegateFactory(
    private val page: ContentIntelligencePage? = null,
) : ContentIntelligenceViewModelDelegateFactory {
    override fun create(scope: CoroutineScope): ContentIntelligenceViewModelDelegate =
        FakeContentIntelligenceViewModelDelegate(page)
}

class FakeContentIntelligenceViewModelDelegate(
    private val page: ContentIntelligencePage? = null,
) : ContentIntelligenceViewModelDelegate {
    private val _uiState = MutableStateFlow(ContentIntelligenceUiState())
    override val uiState: StateFlow<ContentIntelligenceUiState> = _uiState.asStateFlow()

    override fun load(
        ref: ContentIntelligenceRef,
        onPageLoaded: (ContentIntelligencePage) -> Unit,
        onFallbackRequired: () -> Unit,
    ) {
        val resolvedPage = page
        if (resolvedPage == null) {
            onFallbackRequired()
            return
        }

        _uiState.value = ContentIntelligenceUiState(
            page = resolvedPage,
            aiSection = resolvedPage.aiSection,
            graphSection = resolvedPage.graphSection,
            recommendationSection = resolvedPage.recommendationSection,
            digestSection = resolvedPage.digestSection,
            learningRoutesAvailable = resolvedPage.learningRoutesAvailable,
            warnings = resolvedPage.warnings,
        )
        onPageLoaded(resolvedPage)
    }

    override fun retry() {}
}
