package com.duckylife.heritage.modern.feature.directory.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.DirectoryDetailLookup
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DirectoryDetailViewModel.Factory::class)
class DirectoryDetailViewModel @AssistedInject constructor(
    @Assisted("itemId") private val itemId: String?,
    @Assisted("sourceId") private val sourceId: String?,
    @Assisted private val kind: DirectoryItemKind,
    private val repository: HeritageRepository,
) : ViewModel() {
    private val lookup = DirectoryDetailLookup(
        itemId = itemId,
        sourceId = sourceId,
        kind = kind,
    )
    private val _uiState = MutableStateFlow(DirectoryDetailUiState())
    val uiState: StateFlow<DirectoryDetailUiState> = _uiState.asStateFlow()

    init {
        observeCachedItem()
        refresh()
    }

    private fun observeCachedItem() {
        viewModelScope.launch {
            // 先立刻显示缓存详情；网络刷新成功后再用新内容替换。
            repository.cachedDirectoryDetail(lookup).collect { item ->
                if (item != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            item = item,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.item == null,
                    errorMessage = null,
                )
            }
            runCatching {
                repository.refreshDirectoryDetail(lookup)
            }.onSuccess { item ->
                _uiState.value = DirectoryDetailUiState(
                    isLoading = false,
                    item = item,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // 如果缓存里已有内容，保留可读页面并隐藏临时网络错误；
                        // 空页面仍然要把失败原因展示出来。
                        errorMessage = if (it.item == null) throwable.message.orEmpty() else null,
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("itemId") itemId: String?,
            @Assisted("sourceId") sourceId: String?,
            @Assisted kind: DirectoryItemKind,
        ): DirectoryDetailViewModel
    }
}
