package com.duckylife.heritage.modern.feature.detail.intelligence

import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 测试用 ViewModel 委托工厂，返回一个默认空状态的 delegate。
 */
class FakeContentIntelligenceViewModelDelegateFactory : ContentIntelligenceViewModelDelegateFactory {
    override fun create(scope: CoroutineScope): ContentIntelligenceViewModelDelegate =
        FakeContentIntelligenceViewModelDelegate()
}

class FakeContentIntelligenceViewModelDelegate : ContentIntelligenceViewModelDelegate {
    private val _uiState = MutableStateFlow(ContentIntelligenceUiState())
    override val uiState: StateFlow<ContentIntelligenceUiState> = _uiState.asStateFlow()

    override fun load(ref: ContentIntelligenceRef) {}
    override fun retry() {}
}
