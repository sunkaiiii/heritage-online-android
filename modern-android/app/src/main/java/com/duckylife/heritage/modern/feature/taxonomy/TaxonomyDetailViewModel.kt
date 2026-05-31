package com.duckylife.heritage.modern.feature.taxonomy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TaxonomyDetailViewModel.Factory::class)
class TaxonomyDetailViewModel @AssistedInject constructor(
    @Assisted("type") private val type: String,
    @Assisted("key") private val key: String,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaxonomyDetailUiState())
    val uiState: StateFlow<TaxonomyDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }

            when (type) {
                "category" -> {
                    runCatchingCancellable { repository.taxonomyCategoryDetail(key) }
                        .onSuccess { detail ->
                            _uiState.update {
                                it.copy(isLoading = false, categoryDetail = detail)
                            }
                        }
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(isLoading = false, errorKind = e.toUiError().kind)
                            }
                        }
                }
                "region" -> {
                    runCatchingCancellable { repository.taxonomyRegionDetail(key) }
                        .onSuccess { detail ->
                            _uiState.update {
                                it.copy(isLoading = false, regionDetail = detail)
                            }
                        }
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(isLoading = false, errorKind = e.toUiError().kind)
                            }
                        }
                }
                else -> {
                    // kind 类型暂无详情 API，显示 NotFound
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorKind = com.duckylife.heritage.modern.ui.error.ErrorKind.NotFound,
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("type") type: String,
            @Assisted("key") key: String,
        ): TaxonomyDetailViewModel
    }
}
